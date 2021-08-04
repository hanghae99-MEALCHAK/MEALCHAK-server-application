package com.mealchak.mealchakserverapplication.dto.request;

import lombok.Getter;

@Getter
public class FileRequestDto {
    private Long id;
    private String originFileName;
    private String fileName;
    private String filePath;
    private String fileType;
}
