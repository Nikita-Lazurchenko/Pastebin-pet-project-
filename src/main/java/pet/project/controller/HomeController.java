package pet.project.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pet.project.database.entity.*;
import pet.project.dto.PasteCreateDto;

@Controller
public class HomeController{
    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("pasteCreation", new PasteCreateDto());
        model.addAttribute("category", DisplayEnum.getDisplayNames(Category.class));
        model.addAttribute("expiration", Expiration.getDescriptions());
        model.addAttribute("access",  DisplayEnum.getDisplayNames(Access.class));
        model.addAttribute("twoFactorAuthentication", DisplayEnum.getDisplayNames(TwoFactorAuthentication.class));

        return "home";
    }
}
