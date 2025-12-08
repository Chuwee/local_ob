import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EventsDetailPage } from './events-detail.page';

const routes: Routes = [
    {
      path: ':id',
      children: [
        {
          path: '',
          component: EventsDetailPage
        },
        {
          path: ':segment',
          component: EventsDetailPage
        }
      ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class EventsDetailPageRoutingModule { }
