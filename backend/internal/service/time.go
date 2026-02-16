package service

import "time"

func formatISO8601() string {
	return time.Now().UTC().Format(time.RFC3339)
}

func extractYear(date string) string {
	if len(date) >= 4 {
		return date[:4]
	}
	return time.Now().Format("2006")
}
