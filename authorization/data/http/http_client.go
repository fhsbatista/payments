package http

type HttpClient interface {
	Get(url string) error
}