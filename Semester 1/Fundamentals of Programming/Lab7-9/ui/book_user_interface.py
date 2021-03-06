"""
    Author: Mogage Nicolae
    Creation date: 6 nov 2021
    Modul pentru partea de interactiune cu utilizatorul in meniul pentru carti
"""
from error.errors import BookRepositoryError, BookError

class BookUI:
    """
        Clasa pentru interactiunea cu utilizatorul in cadrul meniului de carti
    """
    def __init__(self, books_service):
        """
            Initializarea datelor pentru partea interactiunii cu utilizatorul
        :param books_service: serviciu de carti
        """
        self.__books_service = books_service

    @staticmethod
    def __book_menu():
        """
            Afiseaza meniul principal pentru gestiune carti
        """
        print(" Book menu:\n"
              "\t -add book pentru a adauga o carte.\n"
              "\t -generate pentru a adauga un numar de carti aleatorii.\n"
              "\t -update pentru a accesa meniul de actualizare.\n"
              "\t -delete pentru a accesa meniul de stergere.\n"
              "\t -print pentru a accesa meniul de afisare.\n"
              "\t -show menu pentru a afisa meniul.\n"
              "\t -main menu pentru a va intoarce la meniul principal.\n"
              "\t -exit pentru a iesi din program.")

    @staticmethod
    def __update_book_menu():
        """
            Afiseaza meniul de actualizare a cartilor
        """
        print(" Update menu:\n"
              "\t -titlu pentru a actualiza titlul cartii.\n"
              "\t -descriere pentru a actualiza descrierea cartii.\n"
              "\t -autor pentru a actualiza autorul cartii.\n"
              "\t -book menu pentru a va intoarce la meniul pentru carti.\n"
              "\t -main menu pentru a va intoarce la meniul principal.\n"
              "\t -exit pentru a iesi din program.")

    @staticmethod
    def __delete_book_menu():
        """
            Afiseaza meniul de stergere a cartilor
        """
        print(" Delete menu:\n"
              "\t -id pentru a sterge cartea cu un anumit id.\n"
              "\t -titlu pentru a sterge cartile cu un anumit titlu.\n"
              "\t -descriere pentru a sterge cartile cu un anumit drescriere.\n"
              "\t -autor pentru a sterge cartile cu un anumit autor.\n"
              "\t -book menu pentru a va intoarce la meniul pentru carti.\n"
              "\t -main menu pentru a va intoarce la meniul principal.\n"
              "\t -exit pentru a iesi din program.")

    @staticmethod
    def __print_books_menu():
        """
            Afiseaza meniul de afisare a cartilor
        """
        print(" Print menu:\n"
              "\t -all pentru a afisa toate cartile.\n"
              "\t -id pentru a afisa cartile cu un anumit id.\n"
              "\t -titlu pentru a afisa cartile cu un anumit titlu.\n"
              "\t -descriere pentru a afisa cartile cu un anumit drescriere.\n"
              "\t -autor pentru a afisa cartile cu un anumit autor.\n"
              "\t -book menu pentru a va intoarce la meniul pentru carti.\n"
              "\t -main menu pentru a va intoarce la meniul principal.\n"
              "\t -exit pentru a iesi din program.")

    def __ui_add_book(self):
        """
            Adaugarea unei carti cu id - int, titlu, descriere si autor - string
        """
        try:
            id = int(input("Id: "))
        except ValueError:
            print("Valoare numerica invalida.")
            return
        title = input("Titlu: ")
        description = input("Descriere: ")
        author = input("Autor: ")
        self.__books_service.add_book(id, title, description, author)
        print("Carte adaugata cu succes.")

    def __ui_update_book(self):
        """
            Actualizarea unei carti cu un anumit id - int
        :raise Nu exista nicio carte inregistrata.: daca nu s-a inregistrat nicio carte
        """
        books = self.__books_service.get_all_books()
        if len(books) == 0:
            raise BookRepositoryError("Nu exista nicio carte inregistrata.")

        try:
            id = int(input("Id-ul cartii pe care vreti sa o actualizati: "))
        except ValueError:
            print("Valoare numerica invalida.")
            return

        self.__books_service.find_book_by_id(id)

        self.__update_book_menu()
        while True:
            user_input = input(">>>")
            user_input = user_input.strip()

            if user_input == "exit":
                return "exit"
            elif user_input == "main menu":
                return "main menu"
            elif user_input == "book menu":
                return
            if user_input == "":
                continue
            if user_input == "titlu":
                title = input("Titlu: ")
                try:
                    self.__books_service.update_book_title(id, title)
                except BookError as be:
                    print(be)
                    continue
                print("Titlu actualizat cu succes.")
            elif user_input == "descriere":
                description = input("Descriere: ")
                try:
                    self.__books_service.update_book_description(id, description)
                except BookError as be:
                    print(be)
                    continue
                print("Descriere actualizata cu succes.")
            elif user_input == "autor":
                author = input("Autor: ")
                try:
                    self.__books_service.update_book_author(id, author)
                except BookError as be:
                    print(be)
                    continue
                print("Autor actualizat cu succes.")
            else:
                print("Comanda invalida.")

    def __ui_delete_book(self):
        """
            Meniul pentru stergerea cartilor
        :raise Nu exista nicio carte inregistrata.: daca nu s-a inregistrat nicio carte
        """
        books = self.__books_service.get_all_books()
        if len(books) == 0:
            raise BookRepositoryError("Nu exista nicio carte inregistrata.")

        self.__delete_book_menu()
        while len(books):
            user_input = input(">>>")
            user_input = user_input.strip()

            if user_input == "exit":
                return "exit"
            elif user_input == "main menu":
                return "main menu"
            elif user_input == "book menu":
                return
            if user_input == "":
                continue
            if user_input == "id":
                try:
                    id = int(input("Id: "))
                    self.__books_service.delete_book_by_id(id)
                    print("Carte stearsa cu succes.")
                except ValueError:
                    print("Valoare numerica invalida.")
                except BookError as be:
                    print(be)
                    continue
            elif user_input == "titlu":
                title = input("Titlu: ")
                try:
                    self.__books_service.delete_book_by_title(title)
                except BookError as be:
                    print(be)
                    continue
                print("Carti sterse cu succes.")
            elif user_input == "descriere":
                description = input("Descriere: ")
                try:
                    self.__books_service.delete_book_by_description(description)
                except BookError as be:
                    print(be)
                    continue
                print("Carti sterse cu succes.")
            elif user_input == "autor":
                author = input("Autor: ")
                try:
                    self.__books_service.delete_book_by_author(author)
                except BookError as be:
                    print(be)
                    continue
                print("Carti sterse cu succes.")
            else:
                print("Comanda invalida.")

    def __ui_print_books(self):
        """
            Meniul pentru afisarea cartilor
        :raise Nu exista nicio carte inregistrata.: daca nu s-a inregistrat nicio carte
        """
        books = self.__books_service.get_all_books()
        if len(books) == 0:
            raise BookRepositoryError("Nu exista nicio carte inregistrata.")

        self.__print_books_menu()
        while True:
            user_input = input(">>>")
            user_input = user_input.strip()

            if user_input == "exit":
                return "exit"
            elif user_input == "main menu":
                return "main menu"
            elif user_input == "book menu":
                return
            if user_input == "":
                continue
            if user_input == "all":
                self.__books_service.print_all_books()
            elif user_input == "id":
                try:
                    id = int(input("Id: "))
                    self.__books_service.print_books_id(id)
                except ValueError:
                    print("Valoare numerica invalida.")
                except BookError as be:
                    print(be)
                    continue
            elif user_input == "titlu":
                title = input("Titlu: ")
                try:
                    self.__books_service.print_books_title(title)
                except BookError as be:
                    print(be)
                    continue
            elif user_input == "descriere":
                description = input("Descriere: ")
                try:
                    self.__books_service.print_books_description(description)
                except BookError as be:
                    print(be)
                    continue
            elif user_input == "autor":
                author = input("Autor: ")
                try:
                    self.__books_service.print_books_author(author)
                except BookError as be:
                    print(be)
                    continue
            else:
                print("Comanda invalida.")

    def __ui_generate_books(self):
        try:
            number_of_books = int(input("Numarul de carti care sa se genereze: "))
        except ValueError:
            print("Valoare numerica invalida.")
            return

        self.__books_service.generate_books(number_of_books)

    def book_run(self):
        """
            Meniul principal de gestiune carti
        """
        self.__book_menu()
        while True:
            user_input = input(">>>")
            user_input = user_input.strip()

            if user_input == "exit":
                return True
            elif user_input == "main menu":
                return
            if user_input == "":
                continue
            if user_input == "show menu":
                self.__book_menu()
            elif user_input == "add book":
                try:
                    self.__ui_add_book()
                except BookError as be:
                    print(be)
            elif user_input == "generate":
                try:
                    self.__ui_generate_books()
                except BookError as be:
                    print(be)
            elif user_input == "update":
                try:
                    output = self.__ui_update_book()
                    if output == "exit":
                        return True
                    elif output == "main menu":
                        return
                except BookError as be:
                    print(be)
                self.__book_menu()
            elif user_input == "delete":
                try:
                    output = self.__ui_delete_book()
                    if output == "exit":
                        return True
                    elif output == "main menu":
                        return
                except BookError as be:
                    print(be)
                self.__book_menu()
            elif user_input == "print":
                try:
                    output = self.__ui_print_books()
                    if output == "exit":
                        return True
                    elif output == "main menu":
                        return
                except BookError as be:
                    print(be)
                self.__book_menu()
            else:
                print("Comanda invalida.")
