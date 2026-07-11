import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import HeaderComponent from '@/components/layout/header.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent],
  template: `
  <app-header></app-header>
  <main class="page">
    <router-outlet />
  </main>
  `,
})
export class App {}
