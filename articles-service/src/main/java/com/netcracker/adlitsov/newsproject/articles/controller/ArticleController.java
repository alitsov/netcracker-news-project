package com.netcracker.adlitsov.newsproject.articles.controller;

import com.netcracker.adlitsov.newsproject.articles.exception.ResourceNotFoundException;
import com.netcracker.adlitsov.newsproject.articles.model.Article;
import com.netcracker.adlitsov.newsproject.articles.model.ArticlePreview;
import com.netcracker.adlitsov.newsproject.articles.model.Category;
import com.netcracker.adlitsov.newsproject.articles.repository.ArticleRepository;
import com.netcracker.adlitsov.newsproject.articles.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ArticleController {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping()
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @GetMapping("/previews")
    public List<ArticlePreview> getAllPreviews() {
        return articleRepository.findAll().stream().map(Article::getPreview).collect(Collectors.toList());
    }

    @GetMapping("/previews/sorted")
    public List<ArticlePreview> getAllPreviewsSortedByDate() {
        return articleRepository.findAll()
                                .stream()
                                .map(Article::getPreview)
                                .sorted(Comparator.comparing(ArticlePreview::getAddDate).reversed())
                                .collect(Collectors.toList());
    }

    @GetMapping("/previews/categoryId={id}/sortedByDate")
    public List<ArticlePreview> getAllPreviewsSortedByDate(@PathVariable("id") Integer id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return category.getArticles()
                       .stream()
                       .map(Article::getPreview)
                       .sorted(Comparator.comparing(ArticlePreview::getAddDate).reversed())
                       .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Article getArticleById(@PathVariable("id") Integer id) {
        return articleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));
    }

    @PostMapping()
    public Article createArticle(@Valid @RequestBody Article article) {
        return articleRepository.save(article);
    }



    @PutMapping("/{id}")
    public Article updateArticle(@PathVariable(value = "id") Integer articleId,
                                 @Valid @RequestBody Article articleDetails) {

        Article article = articleRepository.findById(articleId)
                                           .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        article.setTitle(articleDetails.getTitle());
        article.setContent(articleDetails.getContent());
        article.setCategory(articleDetails.getCategory());


        Article updatedArticle = articleRepository.save(article);
        return updatedArticle;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable(value = "id") Integer articleId) {
        Article article = articleRepository.findById(articleId)
                                  .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        articleRepository.delete(article);

        return ResponseEntity.ok().build();
    }
}
