package book.bookspring.global.utils.file;

import book.bookspring.global.exception.custom.BusinessException;
import book.bookspring.global.exception.enums.ErrorCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    private static final String FILE_PATH = "/uploads/profile-images/";
    private static final List<String> ALLOWED_FILE_EXTENSIONS = List.of("jpg", "jpeg", "png",
            "gif");

    private final Path fileStorageLocation;

    public FileService(@Value("${file.dir}") String fileDir) {
        this.fileStorageLocation = Paths.get(fileDir).toAbsolutePath().normalize();
        withIOException(
                () -> Files.createDirectories(this.fileStorageLocation),
                fileDir, "fileStorageLocation"
        );
    }

    public String saveProfileImage(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        // 파일 이름 검증
        validate(originalFileName, name -> name.contains(".."),
                originalFileName, "fileName");

        // 확장자 검증
        String fileExtension = StringUtils.getFilenameExtension(originalFileName)
                .toLowerCase();
        validate(fileExtension, ext -> !ALLOWED_FILE_EXTENSIONS.contains(ext),
                fileExtension, "fileExtension");

        // 고유 이름 생성
        String newFileName = UUID.randomUUID() + "." + fileExtension;
        Path targetLocation = fileStorageLocation.resolve(newFileName);

        // 파일 저장 (공통 예외 처리 추상화)
        withIOException(
                () -> Files.copy(file.getInputStream(), targetLocation,
                        StandardCopyOption.REPLACE_EXISTING),
                originalFileName, "fileName"
        );

        return FILE_PATH + newFileName;
    }

    public void deleteProfileImage(String fileName) {
        Path filePath = fileStorageLocation.resolve(fileName).normalize();
        withIOException(
                () -> Files.deleteIfExists(filePath),
                fileName, "fileName"
        );
    }

    @FunctionalInterface
    private interface ThrowingRunnable {

        void run() throws IOException;
    }

    /**
     * IOException 발생 시 BusinessException으로 전환하여 던져줍니다.
     *
     * @param action 파일 I/O 작업 (파일 생성/삭제/복사 등)
     */
    private void withIOException(ThrowingRunnable action,
            String input, String field) {
        try {
            action.run();
        } catch (IOException ex) {
            throw new BusinessException(input, field, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Predicate를 이용한 검증 후 실패 시 예외를 던집니다.
     */
    private <T> void validate(T value, Predicate<T> predicate,
            String input, String field) {
        if (predicate.test(value)) {
            throw new BusinessException(input, field, ErrorCode.MEMBER_FILE_BAD_REQUEST);
        }
    }
}