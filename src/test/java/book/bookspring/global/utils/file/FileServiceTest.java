package book.bookspring.global.utils.file;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import book.bookspring.global.exception.custom.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@ExtendWith(MockitoExtension.class)
class FileServiceValidationTest {

    @Test
    @DisplayName("saveProfileImage: 파일 이름에 '..' 포함 시 예외 발생")
    void saveProfileImage_invalidFilename_throws(@TempDir Path tempDir) {
        // given
        FileService fileService = new FileService(tempDir.toString());
        MultipartFile file = new MockMultipartFile(
                "file",
                "../hack.png",
                "image/png",
                "data".getBytes()
        );

        // when & then
        assertThrows(BusinessException.class,
                () -> fileService.saveProfileImage(file)
        );
    }

    @Test
    @DisplayName("saveProfileImage: 허용되지 않은 확장자 이용 시 예외 발생")
    void saveProfileImage_invalidExtension_throws(@TempDir Path tempDir) {
        // given
        FileService fileService = new FileService(tempDir.toString());
        MultipartFile file = new MockMultipartFile(
                "file",
                "image.bmp",
                "image/bmp",
                "data".getBytes()
        );

        // when & then
        assertThrows(BusinessException.class,
                () -> fileService.saveProfileImage(file)
        );
    }

    @Test
    @DisplayName("saveProfileImage: 대소문자 확장자 허용")
    void saveProfileImage_uppercaseExtension_allowed(@TempDir Path tempDir) {
        // given
        FileService fileService = new FileService(tempDir.toString());
        MultipartFile file = new MockMultipartFile(
                "file",
                "PHOTO.JPG",
                "image/jpeg",
                "data".getBytes()
        );

        // when & then
        assertDoesNotThrow(() -> fileService.saveProfileImage(file));
    }

    @Test
    @DisplayName("deleteProfileImageByPath: 전체 경로에서 파일명만 추출하여 삭제 호출")
    void deleteProfileImageByPath_extractsFileName(@TempDir Path tempDir) {
        // given
        FileService fileService = spy(new FileService(tempDir.toString()));
        String fullPath = "/uploads/profile-images/pic.png";

        // when
        fileService.deleteProfileImageByPath(fullPath);

        // then
        verify(fileService).deleteProfileImage("pic.png");
    }

    @Test
    @DisplayName("deleteProfileImageByPath: null 또는 빈 문자열일 때 삭제 호출 없음")
    void deleteProfileImageByPath_nullOrBlank(@TempDir Path tempDir) {
        // given
        FileService fileService = spy(new FileService(tempDir.toString()));

        // when & then
        assertDoesNotThrow(() -> fileService.deleteProfileImageByPath(null));
        assertDoesNotThrow(() -> fileService.deleteProfileImageByPath(" "));
        verify(fileService, never()).deleteProfileImage(anyString());
    }
}
