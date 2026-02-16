package service

import (
	"context"
	"regexp"
	"strings"

	"expense-tally-v2/internal/repository"
)

var wordRegex = regexp.MustCompile(`[a-zA-Z0-9]+`)

// KeywordService handles keyword extraction and category suggestion.
type KeywordService struct {
	repo *repository.KeywordRepository
}

// NewKeywordService creates a new KeywordService.
func NewKeywordService(repo *repository.KeywordRepository) *KeywordService {
	return &KeywordService{repo: repo}
}

// Tokenize splits description into keywords: lowercase, remove short words (<3 chars), deduplicate.
func (s *KeywordService) Tokenize(description string) []string {
	words := wordRegex.FindAllString(description, -1)
	seen := make(map[string]bool)
	var result []string
	for _, w := range words {
		lower := strings.ToLower(strings.TrimSpace(w))
		if len(lower) >= 3 && !seen[lower] {
			seen[lower] = true
			result = append(result, lower)
		}
	}
	return result
}

// LearnFromTagging records a keyword-category association when user tags a transaction.
func (s *KeywordService) LearnFromTagging(ctx context.Context, description, merchant, categoryID string) error {
	text := description + " " + merchant
	keywords := s.Tokenize(text)
	now := formatISO8601()
	for _, kw := range keywords {
		pk := "KW#" + kw
		if err := s.repo.IncrementFrequency(ctx, pk, categoryID, now); err != nil {
			return err
		}
	}
	return nil
}

// SuggestCategory returns the top suggested category ID based on keyword frequencies.
func (s *KeywordService) SuggestCategory(ctx context.Context, description, merchant string) (string, error) {
	text := description + " " + merchant
	keywords := s.Tokenize(text)
	if len(keywords) == 0 {
		return "", nil
	}
	assocsByKw, err := s.repo.BatchGetByKeywords(ctx, keywords)
	if err != nil {
		return "", err
	}
	type catScore struct {
		categoryID string
		score     int
	}
	scores := make(map[string]int)
	for _, assocs := range assocsByKw {
		for _, a := range assocs {
			scores[a.CategoryID] += a.Frequency
		}
	}
	var topCategory string
	var topScore int
	for catID, score := range scores {
		if score > topScore {
			topScore = score
			topCategory = catID
		}
	}
	return topCategory, nil
}
