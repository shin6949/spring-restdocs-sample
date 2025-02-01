package me.cocoblue.springrestdocssample.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.springrestdocssample.dto.*;
import me.cocoblue.springrestdocssample.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserApiController {
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<PaginatedResponseDto<UserResponseDto>> getUsers(@RequestParam(name = "cursor", required = false) final Long cursor,
                                                                          @RequestParam(name = "size", required = false, defaultValue = "10") @Min(1) @Max(100) final Integer size) {
        log.debug("getUsers() called with cursor: {}, requestedSize: {}", cursor, size);

        return userService.getUsersList(cursor, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("id") Long id) {
        log.debug("getUser() called with id: {}", id);

        return userService.getUser(id);
    }

    @PostMapping("")
    public ResponseEntity<UserCreateResponseDto> createUser(@RequestBody @Valid UserCreateRequestDto userCreateRequestDto) {
        log.debug("createUser() called with userRequestDto: {}", userCreateRequestDto);

        return userService.createUser(userCreateRequestDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        log.debug("updateUser() called with id: {}, userUpdateRequestDto: {}", id, userUpdateRequestDto);

        return userService.updateUser(id, userUpdateRequestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        log.debug("deleteUser() called with id: {}", id);

        return userService.deleteUser(id);
    }
}
