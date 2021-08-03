package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.response.FileResponseDto;
import com.mealchak.mealchakserverapplication.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Transactional
    public void saveFile(FileResponseDto responseDto) {
        fileRepository.save(responseDto.toEntity()).getId();
    }
}
