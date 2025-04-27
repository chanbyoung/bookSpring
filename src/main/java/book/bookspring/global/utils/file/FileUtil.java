package book.bookspring.global.utils.file;


import book.bookspring.global.exception.custom.BusinessException;
import book.bookspring.global.exception.enums.ErrorCode;
import java.io.IOException;
import java.util.function.Predicate;

/**
 * 파일 관련 공통 유틸 클래스
 */
public class FileUtil {

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws IOException;
    }

    /**
     * 파일 작업 중 IOException 발생 시 BusinessException으로 래핑하여 처리
     *
     * @param action 실행할 파일 작업
     * @param input 예외 메시지에 사용할 입력값
     * @param field 예외 필드 이름
     */
    public static void withIOException(ThrowingRunnable action, String input, String field) {
        try {
            action.run();
        } catch (IOException ex) {
            throw new BusinessException(input, field, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 주어진 조건이 true일 경우 예외를 던지는 검증 유틸
     *
     * @param value      검증할 값
     * @param predicate  실패 조건
     * @param input      예외 메시지에 사용할 입력값
     * @param field      예외 필드 이름
     * @param <T>        검증 타입
     */
    public static <T> void validate(T value, Predicate<T> predicate, String input, String field) {
        if (predicate.test(value)) {
            throw new BusinessException(input, field, ErrorCode.MEMBER_FILE_BAD_REQUEST);
        }
    }
}
