import { Component, signal } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { PopularList } from '../../components/popular-list/popular-list';
import { RecentList } from '../../components/recent-list/recent-list';
import {Router} from '@angular/router';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-home',
  imports: [MatIcon, PopularList, RecentList, FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {

  searchTerm = '';

  constructor(private router: Router) {}

  onSearchClick(){
    this.router.navigate(['/catalog'], {
      queryParams: { q: this.searchTerm }});
  }
}
