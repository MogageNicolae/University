﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Proiect_MPP.domain
{
    public partial class Flight : Entity<int>
    {
        private int freeSeats;
        private int destinationAirport;
        private int departureAirport;
        private DateOnly departureDate;
        private TimeOnly departureTime;

        // Class Constructors //

        public Flight() : base(0)
        {
            this.freeSeats = 0;
            this.destinationAirport = 0;
            this.departureAirport = 0;
            this.departureDate = DateOnly.MinValue;
            this.departureTime = TimeOnly.MinValue;
        }

        public Flight(int id, int freeSeats, int destinationAirport, int departureAirport, DateOnly departureDate, TimeOnly departureTime) : base(id)
        {
            this.freeSeats = freeSeats;
            this.destinationAirport = destinationAirport;
            this.departureAirport = departureAirport;
            this.departureDate = departureDate;
            this.departureTime = departureTime;
        }

        // Getters & Setters //

        public int FreeSeats
        {
            get { return this.freeSeats; }
            set { this.freeSeats = value; }
        }

        public int DestinationAirport
        {
            get { return this.destinationAirport; }
            set { this.destinationAirport = value; }
        }

        public int DepartureAirport
        {
            get { return this.departureAirport; }
            set { this.departureAirport = value; }
        }

        public DateOnly DepartureDate
        {
            get { return this.departureDate; }
            set { this.departureDate = value; }
        }

        public TimeOnly DepartureTime
        {
            get { return this.departureTime; }
            set { this.departureTime = value; }
        }

        // ToString & other functions //

        public override string ToString()
        {
            return "Flight{" +
                "id=" + base.ID +
                ", freeSeats=" + freeSeats +
                ", destination='" + destinationAirport + '\'' +
                ", airportName='" + departureAirport + '\'' +
                ", departureDate=" + departureDate +
                ", departureTime=" + departureTime +
                '}';
        }
    }
}