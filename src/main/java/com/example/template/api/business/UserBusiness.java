package com.example.template.api.business;

import java.util.Optional;

import com.example.template.api.dto.UserDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserBusiness {

    Page<UserDto> findAll(String name, Pageable pageable);

    Optional<UserDto> create(UserDto userDto);

    Optional<UserDto> findById(Long id);

    Optional<UserDto> update(Long id, UserDto userDto);

    void deleteById(Long id);

}
