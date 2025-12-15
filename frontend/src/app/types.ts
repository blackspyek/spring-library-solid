export interface LibraryBranch {
  id: number;
  branchNumber: string;
  name: string;
  city: string;
  address: string;
  latitude: number;
  longitude: number;
  phone?: string;
  email?: string;
  openingHours?: string;
}

export interface User {
  id: number;
  email: string;
  first_name: string;
  last_name: string;
  phone_number: string;
  roles: Role[];
  favouriteBranch?: LibraryBranch;
}
export interface SelectOption {
  label: string;
  value: string | number;
  iconPath?: string;
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
  availableAtBranches?: LibraryBranch[];
}

export type LibrarySelectorMode = 'availability' | 'favorite';

export interface LibrarySelectorDialogData {
  mode: LibrarySelectorMode;
  bookTitle?: string;
  availableBranches?: LibraryBranch[];
  currentFavouriteBranchId?: number;
  /** Optional: provide all branches directly instead of loading from API */
  allBranches?: LibraryBranch[];
}

export interface UserProfile {
  id: number;
  name: string;
  surname: string;
  email: string;
  phone: string;
  readerId: string;
  qrCode: string;
}

export interface ApiTextResponse {
  success: boolean;
  message: string;
}

export interface SingleBook {
  id: number;
  title: string;
  description: string;
  imageUrl: string;
  rentedAt?: string;
  dueDate?: string;
  createdAt?: string;
  releaseYear?: number;
  status: string;
  pageCount: number;
  isbn: string;
  paperType: string;
  publisher: string;
  shelfNumber: number;
  author: string;
  genre: string;
  libraryLocation?: string;
  bestseller?: boolean;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  first: boolean;
  numberOfElements: number;
}

export interface Option {
  value: string;
  label: string;
}

export interface RentalHistoryItem {
  id: number;
  itemTitle: string;
  itemAuthor: string;
  branchName: string;
  branchAddress: string;
  rentedAt: string;
  returnedAt: string;
}
