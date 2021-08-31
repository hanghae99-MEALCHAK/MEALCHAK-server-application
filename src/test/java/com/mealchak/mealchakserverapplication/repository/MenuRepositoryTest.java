package com.mealchak.mealchakserverapplication.repository;

import com.mealchak.mealchakserverapplication.model.Menu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class MenuRepositoryTest {

    @Autowired
    private MenuRepository menuRepository;

    @BeforeEach
    void setUp() throws Exception {
        Menu menu1 = new Menu("중식", 7);
        Menu menu2 = new Menu("일식", 6);
        Menu menu3 = new Menu("한식", 5);
        Menu menu4 = new Menu("분식", 4);
        Menu menu5 = new Menu("카페", 3);
        Menu menu6 = new Menu("양식", 2);
        Menu menu7 = new Menu("기타",1);
        menuRepository.save(menu7);
        menuRepository.save(menu6);
        menuRepository.save(menu5);
        menuRepository.save(menu4);
        menuRepository.save(menu3);
        menuRepository.save(menu2);
        menuRepository.save(menu1);
    }

    @Test
    @DisplayName("findByCategory_중식_성공")
    void findByCategory01() {
        Optional<Menu> result = menuRepository.findByCategory("중식");
        assertEquals(result.get().getCategory(), "중식");
        assertEquals(result.get().getCount(), 7);
    }

    @Test
    @DisplayName("findByCategory_일식_성공")
    void findByCategory02() {
        Optional<Menu> result = menuRepository.findByCategory("일식");
        assertEquals(result.get().getCategory(), "일식");
        assertEquals(result.get().getCount(), 6);
    }

    @Test
    @DisplayName("findByCategory_한식_성공")
    void findByCategory03() {
        Optional<Menu> result = menuRepository.findByCategory("한식");
        assertEquals(result.get().getCategory(), "한식");
        assertEquals(result.get().getCount(), 5);
    }

    @Test
    @DisplayName("findByCategory_분식_성공")
    void findByCategory04() {
        Optional<Menu> result = menuRepository.findByCategory("분식");
        assertEquals(result.get().getCategory(), "분식");
        assertEquals(result.get().getCount(), 4);
    }

    @Test
    @DisplayName("findByCategory_카페_성공")
    void findByCategory05() {
        Optional<Menu> result = menuRepository.findByCategory("카페");
        assertEquals(result.get().getCategory(), "카페");
        assertEquals(result.get().getCount(), 3);
    }

    @Test
    @DisplayName("findByCategory_카페_성공")
    void findByCategory06() {
        Optional<Menu> result = menuRepository.findByCategory("양식");
        assertEquals(result.get().getCategory(), "양식");
        assertEquals(result.get().getCount(), 2);
    }

    @Test
    @DisplayName("findByCategory_기타_성공")
    void findByCategory07() {
        Optional<Menu> result = menuRepository.findByCategory("기타");
        assertEquals(result.get().getCategory(), "기타");
        assertEquals(result.get().getCount(), 1);
    }

//    @Test
//    @DisplayName("findByCategory_Optional_Null_반환")
//    void findByCategory08() {
//        assertThrows(NoSuchElementException.class,
//                () -> menuRepository.findByCategory("dd"));
//    }

    @Test
    @DisplayName("성공_테스트")
    void findAllByOrderByCountDesc() {
        List<Menu> results = menuRepository.findAllByOrderByCountDesc();

        assertEquals(results.get(0).getCategory(),"중식");
        assertEquals(results.get(0).getCount(),7);
        assertEquals(results.get(1).getCategory(),"일식");
        assertEquals(results.get(1).getCount(),6);
        assertEquals(results.get(2).getCategory(),"한식");
        assertEquals(results.get(2).getCount(),5);
        assertEquals(results.get(3).getCategory(),"분식");
        assertEquals(results.get(3).getCount(),4);
        assertEquals(results.get(4).getCategory(),"카페");
        assertEquals(results.get(4).getCount(),3);
        assertEquals(results.get(5).getCategory(),"양식");
        assertEquals(results.get(5).getCount(),2);
        assertEquals(results.get(6).getCategory(),"기타");
        assertEquals(results.get(6).getCount(),1);
    }

}