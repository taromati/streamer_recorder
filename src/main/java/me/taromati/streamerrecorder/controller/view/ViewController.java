package me.taromati.streamerrecorder.controller.view;

import lombok.RequiredArgsConstructor;
import me.taromati.streamerrecorder.manager.StreamerManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class ViewController {

    private final StreamerManager streamerManager;

    @GetMapping("/")
    public String index(
            Model model
    ) {
        model.addAttribute("streamerList", streamerManager.getStreamerList());
        return "index";
    }
}
