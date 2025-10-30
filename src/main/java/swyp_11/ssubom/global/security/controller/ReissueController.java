package swyp_11.ssubom.global.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import swyp_11.ssubom.global.security.service.reissueService;

@RestController
@ResponseBody
@RequiredArgsConstructor
public class ReissueController {
    private final reissueService reissueService;

    @PostMapping("/api/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return reissueService.reissue(request, response);
    }
}
