package com.example.template.api.controller;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.example.commons.api.service.JsonService;
import com.example.template.api.TemplateApiApplication;
import com.example.template.api.model.User;
import com.example.template.api.repository.UserRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TemplateApiApplication.class)
@Transactional
class UserControllerTest {

    private static final String PATH_USER_CONTROLLER = "/template-api/v1/user";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JsonService jsonService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private User userInitial;

    @BeforeEach
    void before() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        this.userInitial = this.userRepository.save(this.userInitial());
    }

    @Test
    void getUser() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
            get(PATH_USER_CONTROLLER).contentType(MediaType.APPLICATION_JSON_VALUE);

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.version", anything()))
            .andExpect(jsonPath("$.meta.server", anything()))
            .andExpect(jsonPath("$.meta.limit", is(50)))
            .andExpect(jsonPath("$.meta.offset", is(0)))
            .andExpect(jsonPath("$.meta.recordCount", is(1)))
            .andExpect(jsonPath("$.meta.totalRecords", is(1)))
            .andExpect(jsonPath("$.records", hasSize(1)))
            .andExpect(jsonPath("$.records[0].id", is(this.userInitial.getId().intValue())))
            .andExpect(jsonPath("$.records[0].name", is(this.userInitial.getName())))
            .andExpect(jsonPath("$.records[0].active", is(this.userInitial.getActive())))
            .andExpect(jsonPath("$.records[0].age", is(this.userInitial.getAge().intValue())))
            .andExpect(jsonPath("$.records[0].height", is(this.userInitial.getHeight())))
            .andExpect(jsonPath("$.records[0].date", is(this.userInitial.getDate().toString())))
            .andExpect(jsonPath("$.records[0].dateTime", is(this.userInitial.getDateTime().toString())));
    }

    @Test
    void getUserFilterByName() throws Exception {
        User userNew = this.userRepository.save(this.userNew());

        MockHttpServletRequestBuilder requestBuilder =
            get(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("name", userNew.getName());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.version", anything()))
            .andExpect(jsonPath("$.meta.server", anything()))
            .andExpect(jsonPath("$.meta.limit", is(50)))
            .andExpect(jsonPath("$.meta.offset", is(0)))
            .andExpect(jsonPath("$.meta.recordCount", is(1)))
            .andExpect(jsonPath("$.meta.totalRecords", is(1)))
            .andExpect(jsonPath("$.records", hasSize(1)))
            .andExpect(jsonPath("$.records[0].id", is(userNew.getId().intValue())))
            .andExpect(jsonPath("$.records[0].name", is(userNew.getName())))
            .andExpect(jsonPath("$.records[0].active", is(userNew.getActive())));
    }

    @Test
    void getUserFilterByNameNotFound() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
            get(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("name", "name not exists");

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.[0].developerMessage", is("User not found")))
            .andExpect(jsonPath("$.[0].userMessage", is("You attempted to get a User, but did not find any")));
    }

    @Test
    void getUserPageable() throws Exception {
        this.userRepository.save(this.userInitial());
        this.userRepository.save(this.userInitial());

        MockHttpServletRequestBuilder requestBuilder =
            get(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("offset", "0")
                .param("limit", "2");

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.version", anything()))
            .andExpect(jsonPath("$.meta.server", anything()))
            .andExpect(jsonPath("$.meta.limit", is(2)))
            .andExpect(jsonPath("$.meta.offset", is(0)))
            .andExpect(jsonPath("$.meta.recordCount", is(2)))
            .andExpect(jsonPath("$.meta.totalRecords", is(3)))
            .andExpect(jsonPath("$.records", hasSize(2)));
    }

    @Test
    void getUserPageableOutOfRange() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
            get(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("offset", "10")
                .param("limit", "10");

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.[0].developerMessage", is("User not found")))
            .andExpect(jsonPath("$.[0].userMessage", is("You attempted to get a User, but did not find any")));
    }

    @Test
    void getUserPageableInvalidOffset() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
            get(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("offset", "abc")
                .param("limit", "10");

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Invalid parameter offset - it must be filled with a valid integer number")))
            .andExpect(jsonPath("$.[0].userMessage", is("Invalid field offset - it must be filled with a valid integer number")));
    }

    @Test
    void getUserPageableInvalidLimit() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
            get(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("offset", "10")
                .param("limit", "abc");

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Invalid parameter limit - it must be filled with a valid integer number")))
            .andExpect(jsonPath("$.[0].userMessage", is("Invalid field limit - it must be filled with a valid integer number")));
    }

    @Test
    void postUser() throws Exception {
        User userCreated = this.userNew();

        MockHttpServletRequestBuilder requestBuilder =
            post(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonService.toJsonString(userCreated));

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.meta.version", anything()))
            .andExpect(jsonPath("$.meta.server", anything()))
            .andExpect(jsonPath("$.meta.limit").doesNotExist())
            .andExpect(jsonPath("$.meta.offset").doesNotExist())
            .andExpect(jsonPath("$.meta.recordCount", is(1)))
            .andExpect(jsonPath("$.meta.totalRecords").doesNotExist())
            .andExpect(jsonPath("$.records", hasSize(1)))
            .andExpect(jsonPath("$.records[0].id", anything()))
            .andExpect(jsonPath("$.records[0].name", is(userCreated.getName())))
            .andExpect(jsonPath("$.records[0].active", is(userCreated.getActive())));
    }

    @Test
    void postUserAlreadyExists() throws Exception {
        User userCreated = this.userInitial;

        ObjectNode userCreatedJson = jsonService.toObjectNode(userCreated);
        userCreatedJson.remove("date");
        userCreatedJson.remove("dateTime");

        MockHttpServletRequestBuilder requestBuilder =
            post(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userCreatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.[0].developerMessage", is("User already exists")))
            .andExpect(jsonPath("$.[0].userMessage", is("You attempted to create User, but already exists")));
    }

    @Test
    void postUserMissingBody() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
            post(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Malformed request")))
            .andExpect(jsonPath("$.[0].userMessage", is("Malformed request")));
    }

    @Test
    void postUserMissingName() throws Exception {
        User userCreated = this.userNew();

        ObjectNode userCreatedJson = jsonService.toObjectNode(userCreated);
        userCreatedJson.remove("name");

        MockHttpServletRequestBuilder requestBuilder =
            post(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userCreatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Missing parameter name")))
            .andExpect(jsonPath("$.[0].userMessage", is("Field name is required and can not be empty")));
    }

    @Test
    void postUserMissingActive() throws Exception {
        User userCreated = this.userNew();

        ObjectNode userCreatedJson = jsonService.toObjectNode(userCreated);
        userCreatedJson.remove("active");

        MockHttpServletRequestBuilder requestBuilder =
            post(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userCreatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Missing parameter active")))
            .andExpect(jsonPath("$.[0].userMessage", is("Field active is required and can not be empty")));
    }

    @Test
    void postUserInvalidActive() throws Exception {
        User userCreated = this.userNew();

        ObjectNode userCreatedJson = jsonService.toObjectNode(userCreated);
        userCreatedJson.put("active", "abc");

        MockHttpServletRequestBuilder requestBuilder =
            post(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userCreatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Invalid parameter active - it must be filled with a valid boolean (true or false)")))
            .andExpect(jsonPath("$.[0].userMessage", is("Invalid field active - it must be filled with a true or false")));
    }

    @Test
    void postUserInvalidAge() throws Exception {
        User userCreated = this.userNew();

        ObjectNode userCreatedJson = jsonService.toObjectNode(userCreated);
        userCreatedJson.put("age", "abc");

        MockHttpServletRequestBuilder requestBuilder =
            post(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userCreatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Invalid parameter age - it must be filled with a valid integer number")))
            .andExpect(jsonPath("$.[0].userMessage", is("Invalid field age - it must be filled with a valid integer number")));
    }

    @Test
    void postUserInvalidHeight() throws Exception {
        User userCreated = this.userNew();

        ObjectNode userCreatedJson = jsonService.toObjectNode(userCreated);
        userCreatedJson.put("height", "abc");

        MockHttpServletRequestBuilder requestBuilder =
            post(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userCreatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Invalid parameter height - it must be filled with a valid number")))
            .andExpect(jsonPath("$.[0].userMessage", is("Invalid field height - it must be filled with a valid number")));
    }

    @Test
    void postUserInvalidDate() throws Exception {
        User userCreated = this.userNew();

        ObjectNode userCreatedJson = jsonService.toObjectNode(userCreated);
        userCreatedJson.put("date", "abc");

        MockHttpServletRequestBuilder requestBuilder =
            post(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userCreatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Invalid parameter date - it must be filled with a valid date in pattern yyyy-MM-dd")))
            .andExpect(jsonPath("$.[0].userMessage", is("Invalid field date - it must be filled with a valid date in pattern yyyy-MM-dd")));
    }

    @Test
    void postUserInvalidDateTime() throws Exception {
        User userCreated = this.userNew();

        ObjectNode userCreatedJson = jsonService.toObjectNode(userCreated);
        userCreatedJson.put("dateTime", "abc");

        MockHttpServletRequestBuilder requestBuilder =
            post(PATH_USER_CONTROLLER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userCreatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Invalid parameter dateTime - it must be filled with a valid date in pattern yyyy-MM-dd'T'HH:mm:ss.SSSZ")))
            .andExpect(jsonPath("$.[0].userMessage", is("Invalid field dateTime - it must be filled with a valid date in pattern yyyy-MM-dd'T'HH:mm:ss.SSSZ")));
    }

    @Test
    void getUserById() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
            get(PATH_USER_CONTROLLER + "/{0}", this.userInitial.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.version", anything()))
            .andExpect(jsonPath("$.meta.server", anything()))
            .andExpect(jsonPath("$.meta.limit").doesNotExist())
            .andExpect(jsonPath("$.meta.offset").doesNotExist())
            .andExpect(jsonPath("$.meta.recordCount", is(1)))
            .andExpect(jsonPath("$.meta.totalRecords").doesNotExist())
            .andExpect(jsonPath("$.records", hasSize(1)))
            .andExpect(jsonPath("$.records[0].id", is(this.userInitial.getId().intValue())))
            .andExpect(jsonPath("$.records[0].name", is(this.userInitial.getName())))
            .andExpect(jsonPath("$.records[0].active", is(this.userInitial.getActive())))
            .andExpect(jsonPath("$.records[0].age", is(this.userInitial.getAge().intValue())))
            .andExpect(jsonPath("$.records[0].height", is(this.userInitial.getHeight())))
            .andExpect(jsonPath("$.records[0].date", is(this.userInitial.getDate().toString())))
            .andExpect(jsonPath("$.records[0].dateTime", is(this.userInitial.getDateTime().toString())));
    }

    @Test
    void getUserByIdNotFound() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
            get(PATH_USER_CONTROLLER + "/{0}", "123")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.[0].developerMessage", is("User not found")))
            .andExpect(jsonPath("$.[0].userMessage", is("You attempted to get a User, but did not find any")));
    }

    @Test
    void getUserByIdInvalidId() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
            get(PATH_USER_CONTROLLER + "/{0}", "abc")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Malformed request")))
            .andExpect(jsonPath("$.[0].userMessage", is("Malformed request")));
    }

    @Test
    void putUser() throws Exception {
        User userUpdated = this.userNew();

        MockHttpServletRequestBuilder requestBuilder =
            put(PATH_USER_CONTROLLER + "/{0}", this.userInitial.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonService.toJsonString(userUpdated));

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.version", anything()))
            .andExpect(jsonPath("$.meta.server", anything()))
            .andExpect(jsonPath("$.meta.limit").doesNotExist())
            .andExpect(jsonPath("$.meta.offset").doesNotExist())
            .andExpect(jsonPath("$.meta.recordCount", is(1)))
            .andExpect(jsonPath("$.meta.totalRecords").doesNotExist())
            .andExpect(jsonPath("$.records", hasSize(1)))
            .andExpect(jsonPath("$.records[0].id", is(this.userInitial.getId().intValue())))
            .andExpect(jsonPath("$.records[0].name", is(userUpdated.getName())))
            .andExpect(jsonPath("$.records[0].active", is(userUpdated.getActive())));
    }

    @Test
    void putUserNotFound() throws Exception {
        User userUpdated = this.userNew();

        MockHttpServletRequestBuilder requestBuilder =
            put(PATH_USER_CONTROLLER + "/{0}", "123")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonService.toJsonString(userUpdated));

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.[0].developerMessage", is("User not found")))
            .andExpect(jsonPath("$.[0].userMessage", is("You attempted to get a User, but did not find any")));
    }

    @Test
    void putUserInvalidId() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
            put(PATH_USER_CONTROLLER + "/{0}", "abc")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Malformed request")))
            .andExpect(jsonPath("$.[0].userMessage", is("Malformed request")));
    }

    @Test
    void putUserMissingName() throws Exception {
        User userUpdated = this.userNew();

        ObjectNode userUpdatedJson = jsonService.toObjectNode(userUpdated);
        userUpdatedJson.remove("name");

        MockHttpServletRequestBuilder requestBuilder =
            put(PATH_USER_CONTROLLER + "/{0}", this.userInitial.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userUpdatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Missing parameter name")))
            .andExpect(jsonPath("$.[0].userMessage", is("Field name is required and can not be empty")));
    }

    @Test
    void putUserMissingActive() throws Exception {
        User userUpdated = this.userNew();

        ObjectNode userUpdatedJson = jsonService.toObjectNode(userUpdated);
        userUpdatedJson.remove("active");

        MockHttpServletRequestBuilder requestBuilder =
            put(PATH_USER_CONTROLLER + "/{0}", this.userInitial.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userUpdatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Missing parameter active")))
            .andExpect(jsonPath("$.[0].userMessage", is("Field active is required and can not be empty")));
    }

    @Test
    void putUserInvalidActive() throws Exception {
        User userUpdated = this.userNew();

        ObjectNode userUpdatedJson = jsonService.toObjectNode(userUpdated);
        userUpdatedJson.put("active", "abc");

        MockHttpServletRequestBuilder requestBuilder =
            put(PATH_USER_CONTROLLER + "/{0}", this.userInitial.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userUpdatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Invalid parameter active - it must be filled with a valid boolean (true or false)")))
            .andExpect(jsonPath("$.[0].userMessage", is("Invalid field active - it must be filled with a true or false")));
    }

    @Test
    void putUserInvalidAge() throws Exception {
        User userUpdated = this.userNew();

        ObjectNode userUpdatedJson = jsonService.toObjectNode(userUpdated);
        userUpdatedJson.put("age", "abc");

        MockHttpServletRequestBuilder requestBuilder =
            put(PATH_USER_CONTROLLER + "/{0}", this.userInitial.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userUpdatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Invalid parameter age - it must be filled with a valid integer number")))
            .andExpect(jsonPath("$.[0].userMessage", is("Invalid field age - it must be filled with a valid integer number")));
    }

    @Test
    void putUserInvalidHeight() throws Exception {
        User userUpdated = this.userNew();

        ObjectNode userUpdatedJson = jsonService.toObjectNode(userUpdated);
        userUpdatedJson.put("height", "abc");

        MockHttpServletRequestBuilder requestBuilder =
            put(PATH_USER_CONTROLLER + "/{0}", this.userInitial.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userUpdatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Invalid parameter height - it must be filled with a valid number")))
            .andExpect(jsonPath("$.[0].userMessage", is("Invalid field height - it must be filled with a valid number")));
    }

    @Test
    void putUserInvalidDate() throws Exception {
        User userUpdated = this.userNew();

        ObjectNode userUpdatedJson = jsonService.toObjectNode(userUpdated);
        userUpdatedJson.put("date", "abc");

        MockHttpServletRequestBuilder requestBuilder =
            put(PATH_USER_CONTROLLER + "/{0}", this.userInitial.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userUpdatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Invalid parameter date - it must be filled with a valid date in pattern yyyy-MM-dd")))
            .andExpect(jsonPath("$.[0].userMessage", is("Invalid field date - it must be filled with a valid date in pattern yyyy-MM-dd")));
    }

    @Test
    void putUserInvalidDateTime() throws Exception {
        User userUpdated = this.userNew();

        ObjectNode userUpdatedJson = jsonService.toObjectNode(userUpdated);
        userUpdatedJson.put("dateTime", "abc");

        MockHttpServletRequestBuilder requestBuilder =
            put(PATH_USER_CONTROLLER + "/{0}", this.userInitial.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userUpdatedJson.toString());

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Invalid parameter dateTime - it must be filled with a valid date in pattern yyyy-MM-dd'T'HH:mm:ss.SSSZ")))
            .andExpect(jsonPath("$.[0].userMessage", is("Invalid field dateTime - it must be filled with a valid date in pattern yyyy-MM-dd'T'HH:mm:ss.SSSZ")));
    }

    @Test
    void deleteUser() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
            delete(PATH_USER_CONTROLLER + "/{0}", this.userInitial.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void deleteUserNotFound() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
            delete(PATH_USER_CONTROLLER + "/{0}", "123")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.[0].developerMessage", is("User not found")))
            .andExpect(jsonPath("$.[0].userMessage", is("You attempted to get a User, but did not find any")));
    }

    @Test
    void deleteUserInvalidId() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
            delete(PATH_USER_CONTROLLER + "/{0}", "abc")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        this.mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[0].developerMessage", is("Malformed request")))
            .andExpect(jsonPath("$.[0].userMessage", is("Malformed request")));
    }

    private User userInitial() {
        return User.builder()
            .name("user")
            .active(true)
            .age(20L)
            .height(1.8)
            .date(LocalDate.of(2000, Month.OCTOBER, 10))
            .dateTime(ZonedDateTime.of(2000, 10, 10, 10, 10, 10, 0, ZoneId.of("Z")))
            .build();
    }

    private User userNew() {
        return User.builder()
            .name("user new")
            .active(true)
            .build();
    }

}
