-- SQL Seed Script for Lublin Library Branches
-- Based on data from https://www.mbp.lublin.pl/filie/kontakt
-- Coordinates are approximate based on Lublin addresses

INSERT INTO library_branches (branch_number, name, city, address, latitude, longitude, phone, email, opening_hours) VALUES
-- Filia nr 1 - ul. Kościelna 7a
('1', 'Filia nr 1', 'Lublin', 'ul. Kościelna 7a', 51.2465, 22.5684, '81 444-58-38', 'filia1@mbp.lublin.pl', 'Poniedziałek: 09:00-18:00|Wtorek: 09:00-18:00|Środa: 09:00-18:00|Czwartek: 09:00-18:00|Piątek: 09:00-18:00|Sobota: Zamknięte|Niedziela: Zamknięte'),

-- Filia nr 6 BIOTEKA MEDIATEKA - Aleje Racławickie 22
('6', 'Filia nr 6 BIOTEKA MEDIATEKA', 'Lublin', 'Aleje Racławickie 22', 51.2463, 22.5312, '81 710-38-73', 'filia6@mbp.lublin.pl', 'Poniedziałek: 10:00-19:00|Wtorek: 10:00-19:00|Środa: 10:00-19:00|Czwartek: 10:00-19:00|Piątek: 10:00-19:00|Sobota: 10:00-14:00|Niedziela: Zamknięte'),

-- Filia nr 12 - ul. Żelazowej Woli 7
('12', 'Filia nr 12', 'Lublin', 'ul. Żelazowej Woli 7', 51.2298, 22.4891, '81 741-52-73', 'filia12@mbp.lublin.pl', 'Poniedziałek: 08:00-16:00|Wtorek: 08:00-16:00|Środa: 08:00-16:00|Czwartek: 08:00-16:00|Piątek: 08:00-16:00|Sobota: Zamknięte|Niedziela: Zamknięte'),

-- Filia nr 18 - ul. Głęboka 8a
('18', 'Filia nr 18', 'Lublin', 'ul. Głęboka 8a', 51.2507, 22.5523, '81 525-10-91', 'filia18@mbp.lublin.pl', 'Poniedziałek: 09:00-17:00|Wtorek: 09:00-17:00|Środa: 09:00-17:00|Czwartek: 09:00-17:00|Piątek: 09:00-17:00|Sobota: 09:00-13:00|Niedziela: Zamknięte'),

-- Filia nr 21 - Rynek 11 (Old Town)
('21', 'Filia nr 21', 'Lublin', 'Rynek 11', 51.2475, 22.5657, '81 532-05-45', 'filia21@mbp.lublin.pl', 'Poniedziałek: 10:00-18:00|Wtorek: 10:00-18:00|Środa: 10:00-18:00|Czwartek: 10:00-18:00|Piątek: 10:00-18:00|Sobota: 10:00-15:00|Niedziela: Zamknięte'),

-- Filia nr 29 - ul. Kiepury 5
('29', 'Filia nr 29', 'Lublin', 'ul. Kiepury 5', 51.2351, 22.4879, '81 741-92-94', 'filia29@mbp.lublin.pl', 'Poniedziałek: 08:30-16:30|Wtorek: 08:30-16:30|Środa: 08:30-16:30|Czwartek: 08:30-16:30|Piątek: 08:30-16:30|Sobota: Zamknięte|Niedziela: Zamknięte'),

-- Filia nr 30 - ul. Braci Wieniawskich 5 (Galeria 31)
('30', 'Filia nr 30', 'Lublin', 'ul. Braci Wieniawskich 5', 51.2375, 22.5002, '81 741-62-36', 'filia30@mbp.lublin.pl', 'Poniedziałek: 10:00-20:00|Wtorek: 10:00-20:00|Środa: 10:00-20:00|Czwartek: 10:00-20:00|Piątek: 10:00-20:00|Sobota: 10:00-16:00|Niedziela: 11:00-15:00'),

-- Filia nr 31 - ul. Nałkowskich 104
('31', 'Filia nr 31', 'Lublin', 'ul. Nałkowskich 104', 51.2276, 22.4934, '81 744-71-69', 'filia31@mbp.lublin.pl', 'Poniedziałek: 09:00-17:00|Wtorek: 09:00-17:00|Środa: 09:00-17:00|Czwartek: 09:00-17:00|Piątek: 09:00-17:00|Sobota: Zamknięte|Niedziela: Zamknięte'),

-- Filia nr 32 BIBLIO MEDIATEKA - ul. Szaserów 13-15
('32', 'Filia nr 32 BIBLIO MEDIATEKA', 'Lublin', 'ul. Szaserów 13-15', 51.2642, 22.5189, '81 311-00-09', 'filia32@mbp.lublin.pl', 'Poniedziałek: 09:00-19:00|Wtorek: 09:00-19:00|Środa: 09:00-19:00|Czwartek: 09:00-19:00|Piątek: 09:00-19:00|Sobota: 10:00-14:00|Niedziela: Zamknięte'),

-- Filia nr 40 BIBLIOTEKA NA POZIOMIE - ul. Sławin 20
('40', 'Filia nr 40 BIBLIOTEKA NA POZIOMIE', 'Lublin', 'ul. Sławin 20', 51.2712, 22.5098, '81 511-10-76', 'filia40@mbp.lublin.pl', 'Poniedziałek: 10:00-18:00|Wtorek: 10:00-18:00|Środa: 10:00-18:00|Czwartek: 10:00-18:00|Piątek: 10:00-18:00|Sobota: 09:00-13:00|Niedziela: Zamknięte');

-- Optional: Update existing items to have availability at random branches
-- This is an example; run after items exist in the database
-- INSERT INTO item_branch_availability (item_id, branch_id)
-- SELECT i.id, b.id 
-- FROM library_item i, library_branches b 
-- WHERE b.id <= 5;
