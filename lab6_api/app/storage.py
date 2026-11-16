from typing import List, Optional
from app.schemas import ApplicationCreate, ApplicationResponse, ApplicationStatus


class InMemoryApplicationStorage:
    def __init__(self) -> None:
        self._items: List[ApplicationResponse] = [
            ApplicationResponse(
                id=1,
                full_name="Логинов Максим Романович",
                education_doc_number="АБ-9876543",
                specialty_code="6-05-0611-01",
                total_score=340,
                status=ApplicationStatus.ADMITTED_TO_EXAMS,
            )
        ]
        self._current_id: int = 1

    def get_all(self) -> List[ApplicationResponse]:
        return self._items

    def get_by_id(self, app_id: int) -> Optional[ApplicationResponse]:
        for item in self._items:
            if item.id == app_id:
                return item
        return None

    def exists_by_doc_number(self, doc_number: str) -> bool:
        normalized = doc_number.strip().lower()
        return any(
            item.education_doc_number.strip().lower() == normalized
            for item in self._items
        )

    def create(self, data: ApplicationCreate) -> ApplicationResponse:
        self._current_id += 1
        new_app = ApplicationResponse(
            id=self._current_id,
            status=ApplicationStatus.REGISTERED,
            **data.model_dump(),
        )
        self._items.append(new_app)
        return new_app

    def delete(self, app_id: int) -> bool:
        for index, item in enumerate(self._items):
            if item.id == app_id:
                self._items.pop(index)
                return True
        return False


storage = InMemoryApplicationStorage()
