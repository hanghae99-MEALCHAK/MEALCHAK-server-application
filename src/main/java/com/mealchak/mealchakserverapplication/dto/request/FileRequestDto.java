package com.mealchak.mealchakserverapplication.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileRequestDto {
    private Long id;
    private String originFileName;
    private String fileName;
    private String filePath;
    private String fileType;
}
