package es.onebox.event.language.dao;

import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Arrays;
import java.util.Collections;

public class LanguageDaoTest extends DaoImplTest {

    @InjectMocks
    private LanguageDao languageDao;

    protected String getDatabaseFile() {
        return "dao/LanguageDao.sql";
    }

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void getIdiomasByCodes() {

        Assertions.assertEquals(0, languageDao.getIdiomasByCodes(Collections.emptyList()).size());

        Assertions.assertEquals(3, languageDao.getIdiomasByCodes(Arrays.asList("es_ES", "ca_ES", "en_US")).size());

        Assertions.assertEquals(3, languageDao.getIdiomasByCodes(Arrays.asList("es_ES", "ca_ES", "en_US", "afasfa")).size());
    }

}
