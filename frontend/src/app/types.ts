export interface User {
  id: number;
  email: string;
  first_name: string;
  last_name: string;
  phone_number: string;
  roles: Role[];
}

export enum Role {
  ROLE_ADMIN,
  ROLE_READER,
  ROLE_LIBRARIAN,
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface Book {
  id: number;
  title: string;
  author: string;
  coverUrl: string;
}
