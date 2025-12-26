package yerong.acorn_post_backend.group.util;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;
import yerong.acorn_post_backend.common.response.ApiException;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.group.repository.GroupRepository;

@Component
public class JoinCodeGenerator {

    private static final String CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final int LENGTH = 6;
    private static final int MAX_TRY = 20;

    private final SecureRandom random = new SecureRandom();
    private final GroupRepository groupRepository;

    public JoinCodeGenerator(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public String generateUnique() {
        for (int i = 0; i < MAX_TRY; i++) {
            String code = randomCode();
            if (!groupRepository.existsByJoinCode(code)) {
                return code;
            }
        }
        throw new ApiException(ErrorCode.JOIN_CODE_GENERATION_FAILED);
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
