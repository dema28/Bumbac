package md.bumbac.api.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Утилита загрузки i18n-файлов (classpath:/i18n/messages_{lang}.json).
 * Читает файл при первом запросе и кэширует в памяти.
 */
@Component
@Slf4j
public class TranslationUtil {

    private static final String PATH_PATTERN = "i18n/messages_%s.json";

    /** кеш: lang → map(key → translation) */
    private final Map<String, Map<String, String>> cache = new ConcurrentHashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Вернуть переводы для выбранного языка.
     * @param langCode  "ru" / "ro" / "en"
     * @return          Map&lt;key, translation&gt;
     */
    public Map<String, String> loadTranslations(String langCode) {
        // уже загружали ― отдадим из кеша
        if (cache.containsKey(langCode)) {
            return cache.get(langCode);
        }

        String path = PATH_PATTERN.formatted(langCode);
        try (var is = new ClassPathResource(path).getInputStream()) {
            Map<String, String> translations =
                    mapper.readValue(is, new TypeReference<>() {});
            cache.put(langCode, translations);
            return translations;
        } catch (IOException e) {
            log.warn("Файл переводов '{}' не найден или испорчен: {}", path, e.getMessage());
            cache.put(langCode, Collections.emptyMap());
            return Collections.emptyMap();
        }
    }
}
