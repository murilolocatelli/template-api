package com.example.template.api.controller;


import java.util.Optional;

import javax.validation.Valid;

import com.example.commons.api.controller.BaseController;
import com.example.commons.api.dto.LimitOffsetPageable;
import com.example.commons.api.dto.ResponseMeta;
import com.example.template.api.business.UserBusiness;
import com.example.template.api.dto.UserDto;
import com.example.template.api.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/template-api/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController extends BaseController {

    @Autowired
    private UserBusiness userBusiness;

    //TODO: use javafaker
    //TODO: wait bug fix of springfox

    public UserController() {
        super(User.class);
    }

    @GetMapping
    public ResponseEntity<ResponseMeta> get(@Valid String name, @Valid LimitOffsetPageable pageable) {
        Page<UserDto> page = this.userBusiness.findAll(name, pageable);

        return super.buildResponse(HttpStatus.OK, page, pageable);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMeta> post(@RequestBody @Valid UserDto userDto) {
        Optional<UserDto> optionalUserDto = this.userBusiness.create(userDto);

        return super.buildResponse(HttpStatus.CREATED, optionalUserDto.orElse(null), null);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseMeta> getById(@PathVariable Long id) {
        Optional<UserDto> optionalUserDto = this.userBusiness.findById(id);

        return super.buildResponse(HttpStatus.OK, optionalUserDto.orElse(null), null);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ResponseMeta> put(@PathVariable Long id, @RequestBody @Valid UserDto userDto) {

        Optional<UserDto> optionalUserDto = this.userBusiness.update(id, userDto);

        return super.buildResponse(HttpStatus.OK, optionalUserDto.orElse(null), null);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseMeta> deleteById(@PathVariable Long id) {
        this.userBusiness.deleteById(id);

        return super.buildResponse(HttpStatus.NO_CONTENT, null, null);
    }

}
