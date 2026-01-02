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
  username: string;
  name: string;
  surname: string;
  phone: string;
  first_name?: string;
  last_name?: string;
  phone_number?: string;
  roles?: Role[];
  authorities?: { authority: string }[];
  favouriteBranch?: LibraryBranch;
  employeeBranch?: LibraryBranch;
}
export interface SelectOption {
  label: string;
  value: string | number;
  iconPath?: string;
}
export enum Role {
  ROLE_ADMIN = 'ROLE_ADMIN',
  ROLE_READER = 'ROLE_READER',
  ROLE_LIBRARIAN = 'ROLE_LIBRARIAN',
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
  bookId?: number;
  availableBranchIds?: number[]; // Branch IDs from API
  currentFavouriteBranchId?: number;
  allBranches?: LibraryBranch[];
}

export interface BookAvailability {
  id: number;
  title: string;
  author: string;
  status: string;
  imageUrl: string;
  daysUntilDue?: number;
  availableAtBranches: number[]; // Array of branch IDs from API
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
export interface ExtendDialogResponse {
  itemId: number;
  branchId: number;
}
export interface ApiTextResponse {
  success: boolean;
  message: string;
}
export type ItemStatus = 'rent' | 'reservation';
export interface ProfileBookItem {
  id: number;
  branchId: number;
  statusType: ItemStatus;
  reservationExpiresAt?: string;
  rentalDueDate?: string;
  isRentExtended?: boolean;

  item: {
    itemId?: number;
    title: string;
    imageUrl?: string;
    author?: string;
    coverText?: string;
  };
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
  availableAtBranches?: LibraryBranch[];
  rentedFromBranchId?: number; // Branch where the item was rented from
  rentExtended?: boolean; // If rental has already been extended
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

export type FeedbackCategory = 'accessibility' | 'navigation' | 'content' | 'other';

export interface FeedbackRequest {
  category: FeedbackCategory;
  message: string;
  pageUrl: string;
}

export interface FeedbackResponse {
  success: boolean;
  message: string;
  ticketId?: string;
}

export interface FeedbackCategoryOption {
  value: FeedbackCategory;
  label: string;
  icon: string;
}

export interface RentRequest {
  libraryItemId: number;
  userId: number;
  branchId: number;
}
