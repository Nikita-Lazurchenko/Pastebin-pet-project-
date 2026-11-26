package pet.project.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.database.entity.*;
import pet.project.dto.PasteCreateDto;
import pet.project.dto.PasteViewDto;
import pet.project.service.PasteService;
import pet.project.service.PasteTwoFactorAuthenticationService;
import pet.project.service.UserService;

@Controller
@RequiredArgsConstructor
public class HomeController{
    private final UserService userService;
    private final PasteService pasteService;
    private final PasteTwoFactorAuthenticationService pasteTwoFactorAuthenticationService;


    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("pasteCreation", new PasteCreateDto());
        model.addAttribute("category", DisplayEnum.getDisplayNames(Category.class));
        model.addAttribute("expiration", Expiration.getDescriptions());
        model.addAttribute("access",  DisplayEnum.getDisplayNames(Access.class));

        return "home";
    }

    @PostMapping("/")
    public String savePaste(@ModelAttribute("pasteCreation") PasteCreateDto pasteCreateDto,
                            RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername("username_default");
        Paste paste = pasteService.save(pasteCreateDto,user);

        redirectAttributes.addFlashAttribute("paste", paste);
        redirectAttributes.addAttribute("text",pasteCreateDto.getPaste());

        return "redirect:/" + paste.getPasteHash();
    }

    @GetMapping("/{hash}")
    public String showPaste(@PathVariable("hash") String hash,
                            @ModelAttribute("paste") Paste paste,
                            @ModelAttribute("text") String text,
                            Model model){
        PasteViewDto pasteViewDto = pasteService.findByHash(hash);

        Access access = Access.valueOf(pasteViewDto.getAccess().toUpperCase());
        if(access.equals(Access.UNLISTED)){
            pasteTwoFactorAuthenticationService.sendLinkToEmail(hash, "lazurchenko.nik@mail.ru");
            return "redirect:/" + hash + "/two-factor-sent";
        } else if (access.equals(Access.PRIVATE)) {
            return "redirect:/" + hash + "/private-paste";
        }

        model.addAttribute("title", pasteViewDto.getTitle());
        model.addAttribute("views", pasteViewDto.getViews());
        model.addAttribute("createdAtRelative", pasteViewDto.getCreatedAtRelative());
        model.addAttribute("text", pasteViewDto.getPaste());
        model.addAttribute("category", pasteViewDto.getCategory());
        model.addAttribute("tags", pasteViewDto.getTag());
        model.addAttribute("expiration",pasteViewDto.getExpiration());

        return "show-paste";
    }

    @GetMapping("/{hash}/two-factor-sent")
    public String twoFactorSent(@PathVariable("hash") String hash,
                            Model model){
        return "two-factor-sent";
    }

    @GetMapping("/{hash}/private-paste")
    public String privatePasteMessage(@PathVariable String hash){
        return "private-paste-message";
    }

    @GetMapping("/{hash}/2fa/{two_factor_hash}")
    public String showPaste(@PathVariable("hash") String hash,
                            @PathVariable("two_factor_hash") String twoFactorHash,
                            @ModelAttribute("paste") Paste paste,
                            @ModelAttribute("text") String text,
                            Model model){

            if(pasteTwoFactorAuthenticationService
                    .checkHashTwoFactorAuthentication(hash, twoFactorHash, "lazurchenko.nik@mail.ru")){
                PasteViewDto pasteViewDto = pasteService.findByHash(hash);

                model.addAttribute("title", pasteViewDto.getTitle());
                model.addAttribute("views", pasteViewDto.getViews());
                model.addAttribute("createdAtRelative", pasteViewDto.getCreatedAtRelative());
                model.addAttribute("text", pasteViewDto.getPaste());
                model.addAttribute("category", pasteViewDto.getCategory());
                model.addAttribute("tags", pasteViewDto.getTag());
                model.addAttribute("expiration",pasteViewDto.getExpiration());
            }else{
                return "redirect:/" + hash + "/2fa"+ "/wrong-two-factor-hash";
            }

        return "show-paste";
    }

    @GetMapping("/{hash}/2fa/wrong-two-factor-hash")
    public String wrongTwoFactorHash(@PathVariable String hash){
        return "wrong-two-factor-hash";
    }

    @GetMapping("/favicon.ico")
    @ResponseBody
    public void returnNoFavicon() {
        // Метод ничего не возвращает и просто гасит запрос браузера.
    }

}
