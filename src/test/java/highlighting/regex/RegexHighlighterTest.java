package highlighting.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.core.HighlightRegion;
import highlighting.presets.MiniJavaColours;
import java.util.List;
import org.junit.jupiter.api.Test;

class RegexHighlighterTest {

  @Test
  void sammeltEinfacheTreffer() {
    RegexHighlighter highlighter = new RegexHighlighter();

    List<HighlightRegion> regions = highlighter.computeRegions("public class Test");

    assertTrue(regions.stream().anyMatch(region -> region.start() == 0 && region.end() == 6));
    assertTrue(regions.stream().anyMatch(region -> region.start() == 7 && region.end() == 12));
  }

  @Test
  void leererTextErzeugtKeineTreffer() {
    RegexHighlighter highlighter = new RegexHighlighter();

    List<HighlightRegion> regions = highlighter.computeRegions("");

    assertTrue(regions.isEmpty());
  }

  @Test
  void textOhneTokenErzeugtKeineTreffer() {
    RegexHighlighter highlighter = new RegexHighlighter();

    List<HighlightRegion> regions = highlighter.computeRegions("abc def ghi");

    assertTrue(regions.isEmpty());
  }

  @Test
  void benachbarteRegionenBleibenErhalten() {
    RegexHighlighter highlighter = new RegexHighlighter();

    List<HighlightRegion> regions =
        List.of(
            new HighlightRegion(0, 5, MiniJavaColours.KEYWORD_COLOUR),
            new HighlightRegion(5, 10, MiniJavaColours.STRING_LITERAL_COLOUR));

    List<HighlightRegion> result = highlighter.resolveConflicts(regions);

    assertEquals(2, result.size());
  }

  @Test
  void ueberlappendeRegionWirdEntfernt() {
    RegexHighlighter highlighter = new RegexHighlighter();

    List<HighlightRegion> regions =
        List.of(
            new HighlightRegion(0, 10, MiniJavaColours.LINE_COMMENT_COLOUR),
            new HighlightRegion(3, 9, MiniJavaColours.KEYWORD_COLOUR));

    List<HighlightRegion> result = highlighter.resolveConflicts(regions);

    assertEquals(1, result.size());
    assertEquals(0, result.get(0).start());
    assertEquals(10, result.get(0).end());
  }

  @Test
  void keywordImKommentarWirdNichtSeparatUebernommen() {
    RegexHighlighter highlighter = new RegexHighlighter();

    String text = "// public class return";
    List<HighlightRegion> regions = highlighter.computeRegions(text);

    assertEquals(1, regions.size());
    assertEquals(0, regions.get(0).start());
    assertEquals(text.length(), regions.get(0).end());
  }

  @Test
  void javadocWirdAlsEineRegionErkannt() {
    RegexHighlighter highlighter = new RegexHighlighter();

    String text = "/** public class */";
    List<HighlightRegion> regions = highlighter.computeRegions(text);

    assertEquals(1, regions.size());
    assertEquals(0, regions.get(0).start());
    assertEquals(text.length(), regions.get(0).end());
  }
}
