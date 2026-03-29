import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-remote-unavailable',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule],
  template: `
    <div class="remote-unavailable">
      <mat-card>
        <mat-card-title>Modulo no disponible</mat-card-title>
        <mat-card-content>
          No se pudo cargar {{ remoteName() }}. Verifica que el remote este levantado e intenta nuevamente.
        </mat-card-content>
        <mat-card-actions>
          <button mat-raised-button color="primary" type="button" (click)="reload()">Reintentar</button>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: [
    `
      .remote-unavailable {
        display: flex;
        justify-content: center;
        padding: 2rem;
      }

      mat-card {
        max-width: 480px;
      }
    `
  ]
})
export class RemoteUnavailableComponent {
  remoteName = input('el modulo solicitado');

  reload(): void {
    window.location.reload();
  }
}
