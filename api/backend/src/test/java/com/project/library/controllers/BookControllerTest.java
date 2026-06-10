package com.project.library.controllers;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.project.library.dto.RequestBookDTO;
import com.project.library.dto.ResponseBookDTO;
import com.project.library.exceptions.EntityNotFoundException;
import com.project.library.factory.BookFactory;
import com.project.library.services.BookService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    private RequestBookDTO requestBookDTO, invalidRequestBookDTO;
    private ResponseBookDTO responseBookDTO;
    private UUID validBookId, invalidBookId;
    private PageImpl<ResponseBookDTO> pageOfBooks;
    private Pageable pageable;

    private static final String BASE_URL = "/books";

    private static final String STRING_WITH_1_CHARACTER = "L";
    private static final String STRING_WITH_101_CHARACTERS = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the i";

    @BeforeEach
    public void setup() {
        validBookId = UUID.randomUUID();
        invalidBookId = UUID.randomUUID();

        requestBookDTO = BookFactory.createRequestBookDTO();
        responseBookDTO = BookFactory.createResponseBookDTO(validBookId);

        pageable = PageRequest.of(0, 10);
        pageOfBooks = new PageImpl<>(List.of(responseBookDTO), pageable, 1);
    }

    @Test
    @DisplayName("registerBook - should return ResponseBookDTO when valid data is provided")
    public void registerBookShouldReturnResponseBookDTOWhenValidDataIsProvided() throws Exception {
        Mockito.when(bookService.createBook(Mockito.any(RequestBookDTO.class))).thenReturn(responseBookDTO);

        String body = objectMapper.writeValueAsString(requestBookDTO);

        mockMvc.perform(post(BASE_URL)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(validBookId.toString()))
                .andExpect(jsonPath("$.title").value(requestBookDTO.title()))
                .andExpect(jsonPath("$.author").value(requestBookDTO.author()))
                .andExpect(jsonPath("$.publishedDate")
                        .value(requestBookDTO.publishedDate().toString()));
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
        Mockito.when(bookService.getBookById(validBookId)).thenReturn(responseBookDTO);

        mockMvc.perform(get(BASE_URL + "/{id}", validBookId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validBookId.toString()))
                .andExpect(jsonPath("$.title").value(responseBookDTO.title()))
                .andExpect(jsonPath("$.author").value(responseBookDTO.author()))
                .andExpect(jsonPath("$.publishedDate")
                        .value(responseBookDTO.publishedDate().toString()));
    }

    @Test
    @DisplayName("getBookById - should return NotFound when invalid id is provided")
    public void getBookByIdShouldReturnNotFoundWhenInvalidIdIsProvided() throws Exception {
        Mockito.doThrow(EntityNotFoundException.class).when(bookService).getBookById(invalidBookId);

        mockMvc.perform(get(BASE_URL + "/{id}", invalidBookId.toString())
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("getAllBooks - should return Page of ResponseBookDTO")
    public void getAllBooksShouldReturnPageOfResponseBookDTO() throws Exception {
        Mockito.when(bookService.getAllBooks(Mockito.any())).thenReturn(pageOfBooks);

        mockMvc.perform(get(BASE_URL)
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("updateBook - should return ResponseBookDTO when valid data is provided")
    public void updateBookShouldReturnResponseBookDTOWhenValidDataIsProvided() throws Exception {
        Mockito.when(bookService.updateBook(Mockito.eq(validBookId), Mockito.any(RequestBookDTO.class)))
                .thenReturn(responseBookDTO);

        String body = objectMapper.writeValueAsString(requestBookDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", validBookId.toString())
                .contentType("application/json")
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validBookId.toString()))
                .andExpect(jsonPath("$.title").value(requestBookDTO.title()))
                .andExpect(jsonPath("$.author").value(requestBookDTO.author()))
                .andExpect(jsonPath("$.publishedDate")
                        .value(requestBookDTO.publishedDate().toString()));
    }

    @Test
    @DisplayName("updateBook - should return NotFound when invalid id is provided")
    public void updateBookShouldReturnNotFoundWhenInvalidIdIsProvided() throws Exception {
        Mockito.doThrow(EntityNotFoundException.class).when(bookService).updateBook(Mockito.eq(invalidBookId),
                Mockito.any(RequestBookDTO.class));

        String body = objectMapper.writeValueAsString(requestBookDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", invalidBookId.toString())
                .contentType("application/json")
                .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("updateBook - should return BadRequest when blank title is provided")
    public void updateBookShouldReturnBadRequestWhenBlankTitleIsProvided() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithTitle("");
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", validBookId.toString())
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("updateBook - should return BadRequest when blank author is provided")
    public void updateBookShouldReturnBadRequestWhenBlankAuthorIsProvided() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithAuthor("");
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", validBookId.toString())
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("updateBook - should return BadRequest when title has less than 2 characters")
    public void updateBookShouldReturnBadRequestWhenTitleHasLessThanTwoCharacters() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithTitle(STRING_WITH_1_CHARACTER);
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", validBookId.toString())
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("updateBook - should return BadRequest when author has less than 2 characters")
    public void updateBookShouldReturnBadRequestWhenAuthorHasLessThanTwoCharacters() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithAuthor(STRING_WITH_1_CHARACTER);
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", validBookId.toString())
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("updateBook - should return BadRequest when title has more than 100 characters")
    public void updateBookShouldReturnBadRequestWhenTitleHasMoreThan100Characters() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithTitle(STRING_WITH_101_CHARACTERS);
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", validBookId.toString())
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("updateBook - should return BadRequest when author has more than 100 characters")
    public void updateBookShouldReturnBadRequestWhenAuthorHasMoreThan100Characters() throws Exception {
        invalidRequestBookDTO = BookFactory.createRequestBookDtoWithAuthor(STRING_WITH_101_CHARACTERS);
        String body = objectMapper.writeValueAsString(invalidRequestBookDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", validBookId.toString())
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("deleteBook - should return NoContent when valid id is provided")
    public void deleteBookShouldReturnNoContentWhenValidIdIsProvided() throws Exception {
        Mockito.doNothing().when(bookService).deleteBook(validBookId);

        mockMvc.perform(delete(BASE_URL + "/{id}", validBookId.toString())
                .contentType("application/json"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("deleteBook - should return NotFound when invalid id is provided")
    public void deleteBookShouldReturnNotFoundWhenInvalidIdIsProvided() throws Exception {
        Mockito.doThrow(EntityNotFoundException.class).when(bookService).deleteBook(invalidBookId);

        mockMvc.perform(delete(BASE_URL + "/{id}", invalidBookId.toString())
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }
}