package book.bookspring.global.utils.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
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
        FileUtil.withIOException(
                () -> Files.createDirectories(this.fileStorageLocation),
                fileDir, "fileStorageLocation"
        );
    }

    public String saveProfileImage(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        // 파일 이름 유효성 검사
        FileUtil.validate(
                originalFileName,
                name -> name.contains(".."),
                originalFileName, "fileName"
        );

        // 확장자 유효성 검사
        String fileExtension = StringUtils.getFilenameExtension(originalFileName).toLowerCase();
        FileUtil.validate(
                fileExtension,
                ext -> !ALLOWED_FILE_EXTENSIONS.contains(ext),
                fileExtension, "fileExtension"
        );

        // 새 파일 이름 생성
        String newFileName = UUID.randomUUID() + "." + fileExtension;
        Path targetLocation = fileStorageLocation.resolve(newFileName);

        // 파일 저장
        FileUtil.withIOException(
                () -> Files.copy(file.getInputStream(), targetLocation,
                        StandardCopyOption.REPLACE_EXISTING),
                originalFileName, "fileName"
        );

        return FILE_PATH + newFileName;
    }

    public void deleteProfileImage(String fileName) {
        Path filePath = fileStorageLocation.resolve(fileName).normalize();
        FileUtil.withIOException(
                () -> Files.deleteIfExists(filePath),
                fileName, "fileName"
        );
    }
}