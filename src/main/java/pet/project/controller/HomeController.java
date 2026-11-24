package pet.project.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.database.entity.*;
import pet.project.dto.PasteCreateDto;
import pet.project.dto.PasteViewDto;
import pet.project.service.PasteService;
import pet.project.service.UserService;

@Controller
@RequiredArgsConstructor
public class HomeController{
    private final UserService userService;
    private final PasteService pasteService;

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

        model.addAttribute("title", pasteViewDto.getTitle());
        model.addAttribute("views", pasteViewDto.getViews());
        model.addAttribute("createdAtRelative", pasteViewDto.getCreatedAtRelative());
        model.addAttribute("text", pasteViewDto.getPaste());
        model.addAttribute("category", pasteViewDto.getCategory());
        model.addAttribute("tags", pasteViewDto.getTag());
        model.addAttribute("expiration",pasteViewDto.getExpiration());

        return "show-paste";
    }

}
