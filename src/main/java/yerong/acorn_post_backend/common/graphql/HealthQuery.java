package yerong.acorn_post_backend.common.graphql;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class HealthQuery {
    @QueryMapping
    public String health() {
        return "ok";
    }
}
