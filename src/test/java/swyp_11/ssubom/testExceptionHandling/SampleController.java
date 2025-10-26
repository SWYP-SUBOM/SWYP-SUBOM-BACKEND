package swyp_11.ssubom.testExceptionHandling;

import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;
import swyp_11.ssubom.global.response.ApiResponse;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/samples")
public class SampleController {

    @PostMapping
    public ApiResponse<String> create(@RequestBody @Valid UserCreateRequest req) {
        // Just echo back for success path
        return ApiResponse.success("ok:" + req.name());
    }

    @GetMapping("/boom")
    public ApiResponse<Void> boom() {
        throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    @GetMapping("/oops")
    public ApiResponse<Void> oops() {
        // triggers catch-all 500
        throw new RuntimeException("unexpected");
    }
}
