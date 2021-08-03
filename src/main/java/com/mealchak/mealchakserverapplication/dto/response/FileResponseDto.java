package com.mealchak.mealchakserverapplication.dto.response;

import com.mealchak.mealchakserverapplication.model.File;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileResponseDto {
    private Long id;
    private String originFileName;
    private String fileName;
    private String filePath;
    private String fileType;

    public File toEntity() {
        return File.builder()
                .id(id)
                .originFileName(originFileName)
                .fileName(fileName)
                .filePath(filePath)
                .fileType(fileType)
                .build();
    }

    @Builder
    public FileResponseDto(Long id, String originFileName, String fileName, String filePath, String fileType) {
        this.id = id;
        this.originFileName = originFileName;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
    }

    public void setFileId(Long fileId) {
        this.id = fileId;
    }
}
