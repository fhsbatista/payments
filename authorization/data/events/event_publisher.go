package events

import "authorization/domain"

type EventPublisher interface {
	Publish(authorization domain.Authorization) error
}