package swyp_11.ssubom.global.nickname;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swyp_11.ssubom.global.error.BusinessException;
import swyp_11.ssubom.global.error.ErrorCode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Component
public class NicknameGenerator {
    private static final int PHASE1_MAX_TRIES = 1;   // Base만
    private static final int PHASE2_MAX_TRIES = 10;  // + Qualifier
    private static final int PHASE3_MAX_TRIES = 30;  // + Activity
    private static final int PHASE4_MAX_TRIES = 59;  // + Both
    private static final int ABSOLUTE_MAX_TRIES = 100;

    private final VocabularyPool vocabularyPool;
    private final NicknamePersistenceHelper persistenceHelper;
//    private final NicknameMetrics metrics;

    @Autowired
    public NicknameGenerator(
            VocabularyPool vocabularyPool,
            NicknamePersistenceHelper persistenceHelper
            ) {
        this.vocabularyPool = vocabularyPool;
        this.persistenceHelper = persistenceHelper;
    }

    public String generateNickname(Long userId) {
        byte[] contextHash = generateContextHash(userId);
        // Base 구성 요소 선택 (전체 시도에서 고정)
        String modifier = selectModifier(contextHash);
        String noun = selectNoun(contextHash);

        int attemptCount = 0;

        //Phase1
        String candidate = buildBase(modifier, noun);
        if (isUnique(candidate)) {
            return candidate;
        }
        attemptCount++;

        //Phase2: Qualifier 추가
        for (int i = 0; i < PHASE2_MAX_TRIES; i++) {
            candidate = buildWithQualifier(contextHash, i, modifier, noun);
            if (isUnique(candidate)) {
                return candidate;
            }
            attemptCount++;
        }

        // Phase 3: Activity 추가
        for (int i = 0; i < PHASE3_MAX_TRIES && attemptCount < ABSOLUTE_MAX_TRIES; i++) {
            candidate = buildWithActivity(contextHash, i + PHASE2_MAX_TRIES, modifier, noun);
            if (isUnique(candidate)) {
                return candidate;
            }
            attemptCount++;
        }

        //Phase4 : Qualifier + Activity
        int phase4Start = PHASE2_MAX_TRIES + PHASE3_MAX_TRIES;
        for (int i = 0; i < PHASE4_MAX_TRIES && attemptCount < ABSOLUTE_MAX_TRIES; i++) {
            candidate = buildWithBoth(contextHash, i + phase4Start, modifier, noun);
            if (isUnique(candidate)) {
                return candidate;
            }
            attemptCount++;
        }

        throw new BusinessException(ErrorCode.NICKNAME_GENERATION_FAILED);

    }

    private byte[] generateContextHash(Long userId) {
        String input = userId + ":" +
                System.currentTimeMillis() + ":" +
                UUID.randomUUID();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private boolean isUnique(String nickname) {
        // DB에 존재하는지 체크만
        if (persistenceHelper.exists(nickname)) {
            return false;
        }
        return true;
    }

    private String selectModifier(byte[] contextHash) {
        int hashInt = readInt(contextHash, 0);
        int index = Math.abs(hashInt) % vocabularyPool.getModifierPoolSize();
        return vocabularyPool.getModifier(index);
    }

    private String selectNoun(byte[] contextHash) {
        int hashInt = readInt(contextHash, 4);
        int index = Math.abs(hashInt) % vocabularyPool.getNounPoolSize();
        return vocabularyPool.getNoun(index);
    }

    private String selectQualifier(byte[] contextHash, int attemptIndex) {
        int hashByte = Byte.toUnsignedInt(contextHash[attemptIndex % contextHash.length]);
        int index = (hashByte + attemptIndex * 7) % vocabularyPool.getQualifierPoolSize();
        return vocabularyPool.getQualifier(index);
    }

    private String selectActivity(byte[] contextHash, int attemptIndex) {
        int hashInt = readInt(contextHash, 8);
        int index = Math.abs(hashInt + attemptIndex * 11) % vocabularyPool.getActivityPoolSize();
        return vocabularyPool.getActivity(index);
    }

    private String buildBase(String modifier, String noun) {
        return modifier + " " + noun;
    }

    private String buildWithQualifier(byte[] contextHash, int attemptIndex,
                                      String modifier, String noun) {
        String qualifier = selectQualifier(contextHash, attemptIndex);
        return qualifier + " " + modifier + " " + noun;
    }

    private String buildWithActivity(byte[] contextHash, int attemptIndex,
                                     String modifier, String noun) {
        String activity = selectActivity(contextHash, attemptIndex);
        return modifier + " " + activity + " " + noun;
    }

    private String buildWithBoth(byte[] contextHash, int attemptIndex,
                                 String modifier, String noun) {
        String qualifier = selectQualifier(contextHash, attemptIndex);
        String activity = selectActivity(contextHash, attemptIndex);
        return qualifier + " " + modifier + " " + activity + " " + noun;
    }

    private int readInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) |
                ((bytes[offset + 1] & 0xFF) << 16) |
                ((bytes[offset + 2] & 0xFF) << 8) |
                (bytes[offset + 3] & 0xFF);
    }

}
