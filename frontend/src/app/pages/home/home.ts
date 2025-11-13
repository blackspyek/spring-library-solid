import { Component, signal } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { Book } from '../../types';
import { PopularList } from '../../components/popular-list/popular-list';
import { RecentList } from '../../components/recent-list/recent-list';

@Component({
  selector: 'app-home',
  imports: [MatIcon, PopularList, RecentList],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {}
