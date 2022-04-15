package com.example.template.api.business.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.commons.api.exception.EntityAlreadyExistsException;
import com.example.commons.api.exception.EntityNotFoundException;
import com.example.template.api.business.UserBusiness;
import com.example.template.api.dto.UserDto;
import com.example.template.api.mapper.UserMapper;
import com.example.template.api.model.User;
import com.example.template.api.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBusinessImpl implements UserBusiness {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper; 

    @Override
    public Page<UserDto> findAll(String name, Pageable pageable) {
        List<UserDto> userDtoList = new ArrayList<>();

        Page<User> page;

        if (name == null) {
            page = this.userRepository.findAll(pageable);
        } else {
            page = this.userRepository.findByName(name, pageable);
        }

        page.forEach(user -> userDtoList.add(this.userMapper.convertUserToUserDto(user)));

        return new PageImpl<>(userDtoList, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    public Optional<UserDto> create(UserDto userDto) {
        User user = this.userMapper.convertUserDtoToUser(userDto);

        boolean userExists = this.userRepository.existsByName(user.getName());

        if (userExists) {
            throw new EntityAlreadyExistsException("User");
        }

        user = this.userRepository.save(user);

        userDto.setId(user.getId());

        return Optional.of(userDto);
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        Optional<User> optionalUser = this.userRepository.findById(id);

        return Optional.ofNullable(this.userMapper.convertUserToUserDto(optionalUser.orElse(null)));
    }

    @Override
    public Optional<UserDto> update(Long id, UserDto userDto) {
        User user = this.userMapper.convertUserDtoToUser(userDto);

        Optional<User> optionalUserSaved = this.userRepository.findById(id);

        if (!optionalUserSaved.isPresent()) {
            throw new EntityNotFoundException("User");
        }

        user.setId(id);

        this.userRepository.save(user);

        return Optional.of(this.userMapper.convertUserToUserDto(user));
    }

    @Override
    public void deleteById(Long id) {
        Optional<User> optionalUser = this.userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            throw new EntityNotFoundException("User");
        }

        this.userRepository.deleteById(id);
    }

}
