#include "service.h"
#include "exceptions.h"
#include <algorithm>
#include <random>

Service::~Service()
{
	Undo* toUndo;
	while (false == UndoActions.empty())
	{
		toUndo = UndoActions.back();
		UndoActions.pop_back();
		delete toUndo;
	}
}

void Service::AddProduct(
	int		Id,
	std::string Name,
	std::string Type,
	std::string Producer,
	int			Price
	)
{
	Product productToAdd(Id, Name, Type, Producer, Price);
	this->Valid.ValidateProduct(productToAdd);
	this->Repo.AddProduct(productToAdd);
	UndoActions.push_back(new UndoAdd{ Repo, Id });
}

void Service::ModifyProduct(
	int			IdProductToModify,
	std::string Name,
	std::string Type,
	std::string Producer,
	int			Price
	)
{
	
	if (!this->Valid.ValidateNumber(IdProductToModify))
	{
		throw ValidationError("Id invalid.\n");
	}
	if (!this->Valid.ValidateNumber(Price))
	{
		throw ValidationError("Pret invalid.\n");
	}

	this->Repo.ModifyProduct(IdProductToModify, Name, Type, Producer, Price);
	Product toModify{ IdProductToModify, Name, Type, Producer, Price };
	UndoActions.push_back(new UndoModify{ Repo, toModify });
}

Product Service::FindProduct(int Id)
{
	if (!this->Valid.ValidateNumber(Id))
	{
		throw ValidationError("Cod de bare invalid.\n");
	}
	return this->Repo.FindProductAfterID(Id);
}
/*
std::vector < Product > Service::FindProduct(int DataToSearch, int FieldToSearch)
{
	std::vector < Product > result;

	switch (FieldToSearch)
	{
	case 1:
		if (!this->Valid.ValidateNumber(DataToSearch))
		{
			throw std::string("Id invalid.\n");
		}
		result = std::vector<Product>(1, this->Repo.FindProductAfterID(DataToSearch));
		break;
	case 5:
		if (!this->Valid.ValidateNumber(DataToSearch))
		{
			throw std::string("Pret invalid.\n");
		}
		result = this->Repo.FindProductsAfterPrice(DataToSearch);
		break;
	default:
		throw std::string("int - FieldToSearch: Invalid value(1/5).\n");
	}

	return result;
}

std::vector < Product > Service::FindProduct(std::string DataToSearch, int FieldToSearch)
{
	std::vector < Product > result;

	switch (FieldToSearch)
	{
	case 2:
		if (!this->Valid.ValidateString(DataToSearch))
		{
			throw std::string("Nume invalid.\n");
		}
		result = this->Repo.FindProductsAfterName(DataToSearch);
		break;
	case 3:
		if (!this->Valid.ValidateString(DataToSearch))
		{
			throw std::string("Tip invalid.\n");
		}
		result = this->Repo.FindProductsAfterType(DataToSearch);
		break;
	case 4:
		if (!this->Valid.ValidateString(DataToSearch))
		{
			throw std::string("Producator invalid.\n");
		}
		result = this->Repo.FindProductsAfterProducer(DataToSearch);
		break;
	default:
		throw std::string("string - FieldToSearch: Invalid value(2-4).\n");
	}

	return result;
}
*/

void Service::DeleteProduct(int Id)
{
	if (!this->Valid.ValidateNumber(Id))
	{
		throw ValidationError("Id invalid.\n");
	}

	Product toDelete = Repo.FindProductAfterID(Id);
	this->Repo.DeleteProduct(Id);
	UndoActions.push_back(new UndoDelete{ Repo, toDelete });
}

std::vector<Product> Service::Filter(int Price) const
{
	if (!this->Valid.ValidateNumber(Price))
	{
		throw ValidationError("Pret invalid.\n");
	}

	return this->Repo.FindProductsAfterPrice(Price);
}

std::vector<Product> Service::Filter(std::string StringData, int FilterType)
{
	if (!this->Valid.ValidateString(StringData))
	{
		throw ValidationError("Date invalide.\n");
	}
	if (FilterType < 1 || FilterType > 2)
	{
		throw ValidationError("Tip filtru invalid.\n");
	}
	
	if (FilterType == 1)
	{
		return this->Repo.FindProductsAfterName(StringData);
	}
	
	return this->Repo.FindProductsAfterProducer(StringData);
}

std::vector<Product> Service::Sort(bool Compare(const Product& Product1, const Product& Product2))
{
	std::vector < Product > products = this->Repo.GetAll();

	std::sort(products.begin(), products.end(), Compare);
	
	return products;
}

std::vector < Product > Service::GetAll() const 
{
	return this->Repo.GetAll();
}

void Service::UndoServ()
{
	if (true == UndoActions.empty())
	{
		throw GeneralExceptions{ "Nu s-au efectuat operatii.\n" };
	}

	Undo* toUndo = UndoActions.back();
	toUndo->doUndo();
	UndoActions.pop_back();
	delete toUndo;
}

// ------------------------------------------------------------------------------------------------------

int ServiceBucket::addToBucket(int Id)
{
	if (false == Valid.ValidateNumber(Id))
	{
		throw ValidationError("Id invalid.\n");
	}
	Product product = Repo.FindProductAfterID(Id);
	if (product.GetId() == -1)
	{
		throw RepositoryError("Nu exista niciun produs cu acest id.\n");
	}

	int price = Bck.add(product);

	notify();

	return price;
}

int ServiceBucket::clearBucket()
{
	Bck.clear();

	notify();

	return 0;
}

int ServiceBucket::generateBucket(int NumberOfProducts)
{
	std::mt19937 mt{ std::random_device{}() };
	std::uniform_int_distribution<> dist(0, (int)Repo.GetSize() - 1);
	int rndNr, totalPrice = Bck.getPrice();
	Product random;

	for (int count = 0; count < NumberOfProducts; count = count + 1)
	{
		rndNr = dist(mt);
		random = Repo.Repo[rndNr];
		totalPrice = Bck.add(random);
	}

	notify();

	return totalPrice;
}

const std::vector < Product >& ServiceBucket::getBucket() const
{
	return Bck.getBucket();
}