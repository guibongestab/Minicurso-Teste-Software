package com.project.library.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.project.library.dto.RequestBookDTO;
import com.project.library.factory.BookFactory;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private RequestBookDTO requestBookDTO, updatedRequestBookDTO, invalidRequestBookDTO;
    private UUID validBookId, invalidBookId;

    private static final String BASE_URL = "/books";
    private static final String STRING_WITH_1_CHARACTER = "L";
    private static final String STRING_WITH_101_CHARACTERS = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the i";

    @BeforeEach
    public void setUp() throws Exception {
        requestBookDTO = BookFactory.createRequestBookDTO();
        updatedRequestBookDTO = BookFactory.createRequestBookDto("Updated Title", "Updated Author", LocalDate.of(2020, 1, 1));
        invalidBookId = UUID.randomUUID();

        String body = objectMapper.writeValueAsString(requestBookDTO);

        MvcResult result = mockMvc.perform(post(BASE_URL)
                .contentType("application/json")
                .content(body))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String id = objectMapper.readTree(responseBody).get("id").asString();
        validBookId = UUID.fromString(id);
    }

    @Test
    @DisplayName("registerBook - should return ResponseBookDTO when valid data is provided")
    public void registerBookShouldReturnResponseBookDTOWhenValidDataIsProvided() throws Exception {
        RequestBookDTO newBook = BookFactory.createRequestBookDTO();
        String body = objectMapper.writeValueAsString(newBook);

        mockMvc.perform(post(BASE_URL)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(newBook.title()))
                .andExpect(jsonPath("$.author").value(newBook.author()))
                .andExpect(jsonPath("$.publishedDate").value(newBook.publishedDate().toString()));
    }

    @Test
    @DisplayName("registerBook - should return BadRequest when blank title is provided")
    public void registerBookShouldReturnBadRequestWhenBlankTitleIsProvided() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithTitle("");
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(post(BASE_URL)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("registerBook - should return BadRequest when blank author is provided")
    public void registerBookShouldReturnBadRequestWhenBlankAuthorIsProvided() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithAuthor("");
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(post(BASE_URL)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("registerBook - should return BadRequest when title has less than 2 characters")
    public void registerBookShouldReturnBadRequestWhenTitleHasLessThan2Characters() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithTitle(STRING_WITH_1_CHARACTER);
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(post(BASE_URL)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("registerBook - should return BadRequest when author has less than 2 characters")
    public void registerBookShouldReturnBadRequestWhenAuthorHasLessThan2Characters() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithAuthor(STRING_WITH_1_CHARACTER);
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(post(BASE_URL)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("registerBook - should return BadRequest when title has more than 100 characters")
    public void registerBookShouldReturnBadRequestWhenTitleHasMoreThan100Characters() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithTitle(STRING_WITH_101_CHARACTERS);
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(post(BASE_URL)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("registerBook - should return BadRequest when author has more than 100 characters")
    public void registerBookShouldReturnBadRequestWhenAuthorHasMoreThan100Characters() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithAuthor(STRING_WITH_101_CHARACTERS);
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(post(BASE_URL)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("getBookById - should return ResponseBookDTO when valid id is provided")
    public void getBookByIdShouldReturnResponseBookDTOWhenValidIdIsProvided() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", validBookId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validBookId.toString()))
                .andExpect(jsonPath("$.title").value(requestBookDTO.title()))
                .andExpect(jsonPath("$.author").value(requestBookDTO.author()))
                .andExpect(jsonPath("$.publishedDate").value(requestBookDTO.publishedDate().toString()));
    }

    @Test
    @DisplayName("getBookById - should return NotFound when invalid id is provided")
    public void getBookByIdShouldReturnNotFoundWhenInvalidIdIsProvided() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", invalidBookId)
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("getAllBooks - should return page of ResponseBookDTO")
    public void getAllBooksShouldReturnPageOfResponseBookDTO() throws Exception {
        mockMvc.perform(get(BASE_URL)
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("updateBook - should return updated ResponseBookDTO when valid data is provided")
    public void updateBookShouldReturnUpdatedResponseBookDTOWhenValidDataIsProvided() throws Exception {
        String body = objectMapper.writeValueAsString(updatedRequestBookDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", validBookId)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validBookId.toString()))
                .andExpect(jsonPath("$.title").value(updatedRequestBookDTO.title()))
                .andExpect(jsonPath("$.author").value(updatedRequestBookDTO.author()))
                .andExpect(jsonPath("$.publishedDate").value(updatedRequestBookDTO.publishedDate().toString()));
    }

    @Test
    @DisplayName("updateBook - should return NotFound when invalid id is provided")
    public void updateBookShouldReturnNotFoundWhenInvalidIdIsProvided() throws Exception {
        String body = objectMapper.writeValueAsString(updatedRequestBookDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", invalidBookId)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("updateBook - should return BadRequest when blank title is provided")
    public void updateBookShouldReturnBadRequestWhenBlankTitleIsProvided() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithTitle("");
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", validBookId)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("updateBook - should return BadRequest when blank author is provided")
    public void updateBookShouldReturnBadRequestWhenBlankAuthorIsProvided() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithAuthor("");
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", validBookId)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("updateBook - should return BadRequest when title has more than 100 characters")
    public void updateBookShouldReturnBadRequestWhenTitleHasMoreThan100Characters() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithTitle(STRING_WITH_101_CHARACTERS);
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", validBookId)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("updateBook - should return BadRequest when author has more than 100 characters")
    public void updateBookShouldReturnBadRequestWhenAuthorHasMoreThan100Characters() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithAuthor(STRING_WITH_101_CHARACTERS);
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", validBookId)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("deleteBook - should return NoContent when valid id is provided")
    public void deleteBookShouldReturnNoContentWhenValidIdIsProvided() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", validBookId)
                .contentType("application/json"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(BASE_URL + "/{id}", validBookId)
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("deleteBook - should return NotFound when invalid id is provided")
    public void deleteBookShouldReturnNotFoundWhenInvalidIdIsProvided() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", invalidBookId)
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }
}