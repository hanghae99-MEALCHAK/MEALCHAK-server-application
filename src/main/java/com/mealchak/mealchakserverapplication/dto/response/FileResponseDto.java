package com.mealchak.mealchakserverapplication.dto.response;

import com.mealchak.mealchakserverapplication.model.File;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FileResponseDto {
    private final String originFileName;
    private final String fileName;
    private final String filePath;
    private final String fileType;

    public File toEntity() {
        return File.builder()
                .originFileName(originFileName)
                .fileName(fileName)
                .filePath(filePath)
                .fileType(fileType)
                .build();
    }

    @Builder
    public FileResponseDto(String originFileName, String fileName, String filePath, String fileType) {
        this.originFileName = originFileName;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
    }
}
