"""
    Author: Mogage Nicolae
    Creation date: 8 nov 2021
    Modul pentru partea de interactiune cu utilizatorul in meniul pentru clienti
"""
from error.errors import ClientRepositoryError, ClientError

class ClientUI:
    """
        Clasa pentru interactiunea cu utilizatorul in cadrul meniului pentru clienti
    """
    def __init__(self, clients_service):
        """
            Initializarea datelor pentru partea de interactiune cu utilizatorul
        :param clients_service: serviciu clienti
        """
        self.__clients_service = clients_service

    @staticmethod
    def __client_menu():
        """
            Afisarea meniului principal de gestiune clienti
        """
        print(" Client menu:\n"
              "\t -add client pentru a adauga un client.\n"
              "\t -generate pentru a adauga un numar de clienti aleatorii.\n"
              "\t -update pentru a accesa meniul de actualizare.\n"
              "\t -delete pentru a accesa meniul de stergere.\n"
              "\t -print pentru a accesa meniul de afisare.\n"
              "\t -show menu pentru a afisa meniul.\n"
              "\t -main menu pentru a va intoarce la meniul principal.\n"
              "\t -exit pentru a iesi din program.")

    @staticmethod
    def __update_client_menu():
        """
            Afisarea meniului de actualizare a clientilor
        """
        print(" Update menu:\n"
              "\t -nume pentru a actualiza numele clientului.\n"
              "\t -client menu pentru a va intoarce la meniul pentru clienti.\n"
              "\t -main menu pentru a va intoarce la meniul principal.\n"
              "\t -exit pentru a iesi din program.")

    @staticmethod
    def __delete_client_menu():
        """
            Afisarea meniului de stergere a clientilor
        """
        print(" Delete menu:\n"
              "\t -id pentru a sterge clientul cu un anumit id.\n"
              "\t -nume pentru a sterge clientii cu un anumit nume.\n"
              "\t -cnp pentru a sterge clientii cu un anumit cnp.\n"
              "\t -client menu pentru a va intoarce la meniul pentru clienti.\n"
              "\t -main menu pentru a va intoarce la meniul principal.\n"
              "\t -exit pentru a iesi din program.")

    @staticmethod
    def __print_client_menu():
        """
            Afisarea meniului de afisare a clientilor
        """
        print(" Print menu:\n"
              "\t -all pentru a afisa toti clientii.\n"
              "\t -id pentru a afisa clientul cu un anumit id.\n"
              "\t -nume pentru a afisa clientii cu un anumit nume.\n"
              "\t -cnp pentru a afisa clientii cu un anumit cnp.\n"
              "\t -prefix pentru a afisa clientii cu un anumit prefix.\n"
              "\t -client menu pentru a va intoarce la meniul pentru clienti.\n"
              "\t -main menu pentru a va intoarce la meniul principal.\n"
              "\t -exit pentru a iesi din program.")

    def __ui_add_client(self):
        """
            Adaugarea unui client cu un id - int, nume - string si cnp - int
        """
        try:
            id = int(input("Id: "))
        except ValueError:
            print("Valoare numerica invalida.")
            return
        nume = input("Nume: ")
        try:
            cnp = int(input("Cnp: "))
        except ValueError:
            print("Valoare numerica invalida.")
            return

        self.__clients_service.add_client(id, nume, cnp)
        print("Client adaugat cu succes.")

    def __ui_update_client(self):
        """
            Actualizarea unui client cu un anumit id int
        :raise Nu exista niciun client inregistrat.: daca nu s-a inregistrat niciun client
        """
        clients = self.__clients_service.get_all_clients()
        if len(clients) == 0:
            raise ClientRepositoryError("Nu exista niciun client inregistrat.")

        try:
            id = int(input("Id-ul clientului pe care vreti sa il actualizati: "))
        except ValueError:
            print("Valoare numerica invalida.")
            return

        self.__clients_service.find_client_by_id(id)

        self.__update_client_menu()
        while True:
            user_input = input(">>>")
            user_input = user_input.strip()

            if user_input == "exit":
                return "exit"
            elif user_input == "main menu":
                return "main menu"
            elif user_input == "client menu":
                return
            if user_input == "":
                continue
            if user_input == "nume":
                name = input("Nume: ")
                try:
                    self.__clients_service.update_client_name(id, name)
                except ClientError as ce:
                    print(ce)
                print("Nume actualizat cu succes.")
            else:
                print("Comanda invalida.")

    def __ui_delete_client(self):
        """
            Stergerea clientilor
        :raise Nu exista niciun client inregistrat.: daca nu s-a inregistrat niciun client
        """
        clients = self.__clients_service.get_all_clients()
        if len(clients) == 0:
            raise ClientRepositoryError("Nu exista niciun client inregistrat.")

        self.__delete_client_menu()
        while len(clients):
            user_input = input(">>>")
            user_input = user_input.strip()

            if user_input == "exit":
                return "exit"
            elif user_input == "main menu":
                return "main menu"
            elif user_input == "client menu":
                return
            if user_input == "":
                continue
            if user_input == "id":
                try:
                    id = int(input("Id: "))
                    self.__clients_service.delete_client_by_id(id)
                    print("Client sters cu succes.")
                except ValueError:
                    print("Valoare numerica invalida.")
                except ClientError as ce:
                    print(ce)
            elif user_input == "nume":
                name = input("Nume: ")
                try:
                    self.__clients_service.delete_client_by_name(name)
                except ClientError as ce:
                    print(ce)
                print("Clienti stersi cu succes.")
            elif user_input == "cnp":
                try:
                    cnp = int(input("Cnp: "))
                    self.__clients_service.delete_client_by_cnp(cnp)
                    print("Clienti stersi cu succes.")
                except ValueError:
                    print("Valoare numerica invalida.")
                except ClientError as ce:
                    print(ce)
            else:
                print("Comanda invalida.")

    def __ui_print_clients(self):
        """
           Afisarea clientilor
        :raise Nu exista niciun client inregistrat.: daca nu s-a inregistrat niciun client
        """
        clients = self.__clients_service.get_all_clients()
        if len(clients) == 0:
            raise ClientRepositoryError("Nu exista niciun client inregistrat.")

        self.__print_client_menu()
        while True:
            user_input = input(">>>")
            user_input = user_input.strip()

            if user_input == "exit":
                return "exit"
            elif user_input == "main menu":
                return "main menu"
            elif user_input == "client menu":
                return
            if user_input == "":
                continue
            if user_input == "all":
                self.__clients_service.print_all_clients()
            elif user_input == "id":
                try:
                    id = int(input("Id: "))
                    self.__clients_service.print_clients_id(id)
                except ValueError:
                    print("Valoare numerica invalida.")
                except ClientError as ce:
                    print(ce)
            elif user_input == "nume":
                name = input("Nume: ")
                try:
                    self.__clients_service.print_clients_name(name)
                except ClientError as ce:
                    print(ce)
            elif user_input == "cnp":
                try:
                    cnp = int(input("Cnp: "))
                    self.__clients_service.print_clients_cnp(cnp)
                except ValueError:
                    print("Valoare numerica invalida.")
                except ClientError as ce:
                    print(ce)
            elif user_input == "prefix":
                prefix = input("Prefix: ")
                try:
                    self.__clients_service.print_filter_name_by_prefix(prefix.lower())
                except ClientError as ce:
                    print(ce)
            else:
                print("Comanda invalida.")

    def __ui_generate_client(self):
        """
            Genereaza un numar de clienti introdus de la utilizator
        """
        try:
            number_of_clients = int(input("Numarul de clienti care sa se genereze: "))
        except ValueError:
            print("Valoare numerica invalida.")
            return

        self.__clients_service.generate_clients(number_of_clients)

    def client_run(self):
        """
            Meniul principal pentru gestiune clienti
        """
        self.__client_menu()
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
                self.__client_menu()
            elif user_input == "add client":
                try:
                    self.__ui_add_client()
                except ClientError as ce:
                    print(ce)
            elif user_input == "generate":
                try:
                    self.__ui_generate_client()
                except ClientError as ce:
                    print(ce)
            elif user_input == "update":
                try:
                    exit = self.__ui_update_client()
                    if exit == "exit":
                        return "exit"
                    elif exit == "main menu":
                        return
                except ClientError as ce:
                    print(ce)
                self.__client_menu()
            elif user_input == "delete":
                try:
                    exit = self.__ui_delete_client()
                    if exit == "exit":
                        return "exit"
                    elif exit == "main menu":
                        return
                except ClientError as ce:
                    print(ce)
                self.__client_menu()
            elif user_input == "print":
                try:
                    exit = self.__ui_print_clients()
                    if exit == "exit":
                        return "exit"
                    elif exit == "main menu":
                        return
                except ClientError as ce:
                    print(ce)
                self.__client_menu()
            else:
                print("Comanda invalida.")
