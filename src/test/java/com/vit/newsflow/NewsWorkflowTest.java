package com.vit.newsflow;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NewsWorkflowTest {

    private static String words(int n) { return "news ".repeat(n).trim(); }

    private static Article goodArticle() {
        return new Article("Council approves metro", "council-approves-metro", "Asha Rao",
                words(30), "https://example.com/m.jpg", "Metro train");
    }

    @Test
    public void validArticleHasNoErrors() {
        assertTrue(new ArticleValidator().validate(goodArticle()).isEmpty());
    }

    @Test
    public void missingTitleFailsValidation() {
        Article a = new Article("", "a-b", "Asha", words(30), null, null);
        assertFalse(new ArticleValidator().validate(a).isEmpty());
    }

    @Test
    public void badSlugFailsValidation() {
        Article a = new Article("T", "Bad Slug", "Asha", words(30), null, null);
        assertFalse(new ArticleValidator().validate(a).isEmpty());
    }

    @Test
    public void imageWithoutAltTextFails() {
        Article a = new Article("T", "a-b", "Asha", words(30), "i.jpg", "");
        assertFalse(new ArticleValidator().validate(a).isEmpty());
    }

    @Test
    public void validSubmitMovesToPendingReview() {
        NewsWorkflowService svc = new NewsWorkflowService();
        Article a = goodArticle();
        svc.submit(a);
        assertEquals(Article.Status.PENDING_REVIEW, a.status);
    }

    @Test
    public void invalidSubmitMovesToValidationFailed() {
        NewsWorkflowService svc = new NewsWorkflowService();
        Article a = new Article("", "x", "", "short", null, null);
        svc.submit(a);
        assertEquals(Article.Status.VALIDATION_FAILED, a.status);
    }

    @Test
    public void approveMovesToApproved() {
        NewsWorkflowService svc = new NewsWorkflowService();
        Article a = goodArticle();
        svc.submit(a);
        svc.approve(a, "Meera");
        assertEquals(Article.Status.APPROVED, a.status);
    }

    @Test
    public void publishMovesToPublishedAndRendersHtml() {
        NewsWorkflowService svc = new NewsWorkflowService();
        Article a = goodArticle();
        svc.submit(a);
        svc.approve(a, "Meera");
        String html = svc.publish(a);
        assertEquals(Article.Status.PUBLISHED, a.status);
        assertTrue(html.contains("<h1>Council approves metro</h1>"));
    }

    @Test
    public void historyRecordsEachTransition() {
        NewsWorkflowService svc = new NewsWorkflowService();
        Article a = goodArticle();
        svc.submit(a);
        svc.approve(a, "Meera");
        svc.publish(a);
        assertEquals(3, a.history.size());
    }

    @Test
    public void rejectWithoutCommentThrows() {
        NewsWorkflowService svc = new NewsWorkflowService();
        Article a = goodArticle();
        svc.submit(a);
        assertThrows(IllegalArgumentException.class, () -> svc.reject(a, "Meera", " "));
    }

    @Test
    public void rejectWithCommentMovesToRejected() {
        NewsWorkflowService svc = new NewsWorkflowService();
        Article a = goodArticle();
        svc.submit(a);
        svc.reject(a, "Meera", "Needs a second source");
        assertEquals(Article.Status.REJECTED, a.status);
    }

    @Test
    public void cannotPublishUnapprovedArticle() {
        NewsWorkflowService svc = new NewsWorkflowService();
        Article a = goodArticle();
        svc.submit(a);
        assertThrows(IllegalStateException.class, () -> svc.publish(a));
    }
}
