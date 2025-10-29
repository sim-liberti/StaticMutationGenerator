package org.unina;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.unina.MutationRules.*;
import org.unina.Utils.RandomSelector;

/**
 * Unit test for simple App.
 */
public class MutationRulesTest {
    long seed = 1234;
    String htmlString =
        """
            <div>
                <p>test paragraph</p>
                <div>
                    <span>test paragraph</span>
                    <a>test paragraph</a>
                    <div>
                        <p>test paragraph</p>
                    </div>
                    <p>test paragraph</p>
                </div>
                <h1>test paragraph</h1>
                <main class="test-class" id="test-id" class="test-class" data-target="modal" data-text="text">This is a main tag.</main>
            </div>
            """;

    @Test
    public void testApplyAttributeIdentifierModificationRule_shouldReturnTrue() {
        // Arrange
        Document document = Jsoup.parse(htmlString);
        Element target = document.selectFirst(".test-class");
        assertNotNull(target, "Target element should not be null");

        MutationRule mutation = new AttributeIdentifierModificationRule();

        // Act
        assertTrue(mutation.ApplyMutation(target));
    }

    @Test
    public void testApplyAttributeRemovalRule_shouldReturnTrue() {
        // Arrange
        Document document = Jsoup.parse(htmlString);
        Element target = document.selectFirst(".test-class");
        assertNotNull(target, "Target element should not be null");

        MutationRule mutation = new AttributeRemovalRule();

        // Act
        assertTrue(mutation.ApplyMutation(target));
    }

    @Test
    public void testApplyAttributeValueModificationRule_shouldReturnTrue() {
        // Arrange
        Document document = Jsoup.parse(htmlString);
        Element target = document.selectFirst(".test-class");
        assertNotNull(target, "Target element should not be null");

        MutationRule mutation = new AttributeValueModificationRule();

        // Act
        assertTrue(mutation.ApplyMutation(target));
    }

    @Test
    public void testApplyTagInsertionRule_shouldReturnTrue() {
        // Arrange
        Document document = Jsoup.parse(htmlString);
        Element target = document.selectFirst(".test-class");
        assertNotNull(target, "Target element should not be null");

        MutationRule mutation = new TagInsertionRule();

        // Act
        assertTrue(mutation.ApplyMutation(target));
    }

    @Test
    public void testApplyTagMovementToAnyHtmlTreePointRule_shouldReturnTrue() {
        // Arrange
        RandomSelector.initialize(seed);
        Document document = Jsoup.parse(htmlString);
        Element target = document.selectFirst(".test-class");
        assertNotNull(target, "Target element should not be null");

        MutationRule mutation = new TagMovementToAnyHtmlTreePointRule();

        // Act
        assertTrue(mutation.ApplyMutation(target));
    }

    @Test
    public void testApplyTagMovementWithinContainerRule_shouldReturnTrue() {
        // Arrange
        RandomSelector.initialize(seed);
        Document document = Jsoup.parse(htmlString);
        Element target = document.selectFirst(".test-class");
        assertNotNull(target, "Target element should not be null");

        MutationRule mutation = new TagMovementWithinContainerRule();

        // Act
        assertTrue(mutation.ApplyMutation(target));
    }

    @Test
    public void testApplyTagRemovalRule_shouldReturnTrue() {
        // Arrange
        Document document = Jsoup.parse(htmlString);
        Element target = document.selectFirst(".test-class");
        assertNotNull(target, "Target element should not be null");

        MutationRule mutation = new TagRemovalRule();

        // Act
        assertTrue(mutation.ApplyMutation(target));
    }

    @Test
    public void testApplyTagTypeModificationRule_shouldReturnTrue() {
        // Arrange
        Document document = Jsoup.parse(htmlString);
        Element target = document.selectFirst(".test-class");
        assertNotNull(target, "Target element should not be null");

        MutationRule mutation = new TagTypeModificationRule();

        // Act
        assertTrue(mutation.ApplyMutation(target));
    }


    @Test
    public void testApplyTextContentModificationRule_shouldReturnTrue() {
        // Arrange
        RandomSelector.initialize(seed);
        Document document = Jsoup.parse(htmlString);
        Element target = document.selectFirst(".test-class");
        assertNotNull(target, "Target element should not be null");

        MutationRule mutation = new TextContentModificationRule();

        // Act
        assertTrue(mutation.ApplyMutation(target));
    }

    @Test
    public void testApplyTextContentRemovalRule_shouldReturnTrue() {
        // Arrange
        RandomSelector.initialize(seed);
        Document document = Jsoup.parse(htmlString);
        Element target = document.selectFirst(".test-class");
        assertNotNull(target, "Target element should not be null");

        MutationRule mutation = new TextContentRemovalRule();

        // Act
        assertTrue(mutation.ApplyMutation(target));
    }
}
