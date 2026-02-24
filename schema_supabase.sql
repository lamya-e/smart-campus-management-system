-- Smart Campus Management System - Supabase schema (dev, RLS disabled)

-- Enable UUID generation extension (if not already enabled)
create extension if not exists "pgcrypto";

-- Étudiants
create table if not exists etudiants (
    id uuid primary key default gen_random_uuid(),
    cne text not null unique,
    nom text not null,
    prenom text not null,
    filiere text,
    niveau text
);
alter table etudiants disable row level security;

-- Professeurs
create table if not exists professeurs (
    id uuid primary key default gen_random_uuid(),
    code text not null unique,
    nom text not null,
    prenom text not null,
    specialite text,
    email text
);
alter table professeurs disable row level security;

-- Modules
create table if not exists modules (
    id uuid primary key default gen_random_uuid(),
    code text not null unique,
    nom text not null,
    coefficient integer not null,
    semestre text,
    professeur_id uuid references professeurs(id)
);
alter table modules disable row level security;

-- Notes
create table if not exists notes (
    id uuid primary key default gen_random_uuid(),
    etudiant_id uuid not null references etudiants(id) on delete cascade,
    module_id uuid not null references modules(id) on delete cascade,
    note_normale double precision not null,
    note_rattrapage double precision,
    annee_universitaire text not null,
    unique (etudiant_id, module_id, annee_universitaire)
);
alter table notes disable row level security;

