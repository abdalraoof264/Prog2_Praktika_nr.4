package highlighting.presets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

class MiniJavaTokensTest {

  private List<HighlightRegion> collectAllMatches(String text) {
    return MiniJavaTokens.defaultTokens().stream()
        .flatMap(token -> token.test(text).stream())
        .toList();
  }

  @Test
  void erkenntStringLiteral() {
    List<HighlightRegion> regions = collectAllMatches("\"Hallo\"");

    assertTrue(regions.stream().anyMatch(region -> region.start() == 0 && region.end() == 7));
  }

  @Test
  void erkenntCharacterLiteral() {
    List<HighlightRegion> regions = collectAllMatches("'a'");

    assertTrue(regions.stream().anyMatch(region -> region.start() == 0 && region.end() == 3));
  }

  @Test
  void erkenntKeywordAlsGanzesWort() {
    List<HighlightRegion> regions = collectAllMatches("public class Test");

    assertTrue(regions.stream().anyMatch(region -> region.start() == 0 && region.end() == 6));
    assertTrue(regions.stream().anyMatch(region -> region.start() == 7 && region.end() == 12));
  }

  @Test
  void erkenntKeywordNichtInnerhalbVonIdentifier() {
    List<HighlightRegion> regions = collectAllMatches("publicValue");

    assertFalse(regions.stream().anyMatch(region -> region.start() == 0 && region.end() == 6));
  }

  @Test
  void erkenntAnnotation() {
    List<HighlightRegion> regions = collectAllMatches("@Override");

    assertTrue(regions.stream().anyMatch(region -> region.start() == 0 && region.end() == 9));
  }

  @Test
  void erkenntZeilenKommentar() {
    List<HighlightRegion> regions = collectAllMatches("int x; // Kommentar");

    assertTrue(regions.stream().anyMatch(region -> region.start() == 7 && region.end() == 19));
  }

  @Test
  void erkenntBlockKommentar() {
    List<HighlightRegion> regions = collectAllMatches("a /* Kommentar */ b");

    assertTrue(regions.stream().anyMatch(region -> region.start() == 2 && region.end() == 17));
  }

  @Test
  void erkenntJavadocKommentar() {
    List<HighlightRegion> regions = collectAllMatches("/** Kommentar */");

    assertTrue(regions.stream().anyMatch(region -> region.start() == 0 && region.end() == 16));
  }

  @Test
  void erkenntMehrereTreffer() {
    List<HighlightRegion> regions = collectAllMatches("public return null");

    assertTrue(regions.size() >= 3);
  }

  @Test
  void keinTrefferBeiNormalemText() {
    List<HighlightRegion> regions = collectAllMatches("abc def ghi");

    assertTrue(regions.isEmpty());
  }
}
