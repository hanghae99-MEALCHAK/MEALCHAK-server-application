package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.FileRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.FileResponseDto;
import com.mealchak.mealchakserverapplication.model.File;
import com.mealchak.mealchakserverapplication.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Transactional
    public Long saveFile(FileResponseDto responseDto) {
        return fileRepository.save(responseDto.toEntity()).getId();
    }

    @Transactional
    public FileResponseDto getFile(Long id) {
        File file = fileRepository.findById(id).get();

        FileResponseDto fileResponseDto = FileResponseDto.builder()
                .id(id)
                .originFileName(file.getOriginFileName())
                .fileName(file.getFileName())
                .filePath(file.getFilePath())
                .fileType(file.getFileType())
                .build();
        return fileResponseDto;
    }
}
