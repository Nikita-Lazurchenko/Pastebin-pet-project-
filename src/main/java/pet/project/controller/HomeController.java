package pet.project.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.project.database.entity.*;
import pet.project.dto.PasteCreateDto;
import pet.project.service.PasteService;
import pet.project.service.UserService;

import java.security.Principal;

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

        System.out.println(paste.getPasteHash());

        return "redirect:/";//+paste.getPasteHash();
    }

}
