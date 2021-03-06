"""
    Author: Mogage Nicolae
    Creation date: 6 nov 2021
    Modul de unde incepe aplicatia
"""
from testing.pyunit_tests import run_tests
from ui.main_user_interface import Console
from service.book_service import BooksService
from service.client_service import ClientsService
from service.rent_service import RentService
from validator.validators import BooksValidator, ClientsValidator, RentValidator
from repository.book_repository import BooksFileRepository
from repository.client_repository import ClientsFileRepository
from repository.rent_repository import RentFileRepository

if __name__ == "__main__":
    books_validator = BooksValidator()
    clients_validator = ClientsValidator()
    rent_validator = RentValidator()

    books_repository = BooksFileRepository("Save_files/book.txt")
    clients_repository = ClientsFileRepository("Save_files/client.txt")
    rent_repository = RentFileRepository("Save_files/rent.txt")

    books_service = BooksService(books_repository, books_validator)
    clients_service = ClientsService(clients_repository, clients_validator)
    rent_service = RentService(rent_repository, rent_validator, books_repository, clients_repository)

    run_tests()

    ui = Console(books_service, clients_service, rent_service)
    ui.run()
    print("The program has ended successfully.")
