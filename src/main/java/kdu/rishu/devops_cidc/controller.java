package kdu.rishu.devops_cidc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controller {

    @Value("${spring.application.version}")
    private String appVersion;

    @GetMapping("version")
    public ResponseEntity<Integer> hello() {
        return ResponseEntity.ok(Integer.parseInt(appVersion));
    }

}
