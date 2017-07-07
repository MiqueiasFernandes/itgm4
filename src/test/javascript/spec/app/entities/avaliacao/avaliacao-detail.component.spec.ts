import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { DateUtils, DataUtils, EventManager } from 'ng-jhipster';
import { ItgmTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { AvaliacaoDetailComponent } from '../../../../../../main/webapp/app/entities/avaliacao/avaliacao-detail.component';
import { AvaliacaoService } from '../../../../../../main/webapp/app/entities/avaliacao/avaliacao.service';
import { Avaliacao } from '../../../../../../main/webapp/app/entities/avaliacao/avaliacao.model';

describe('Component Tests', () => {

    describe('Avaliacao Management Detail Component', () => {
        let comp: AvaliacaoDetailComponent;
        let fixture: ComponentFixture<AvaliacaoDetailComponent>;
        let service: AvaliacaoService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [ItgmTestModule],
                declarations: [AvaliacaoDetailComponent],
                providers: [
                    DateUtils,
                    DataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    AvaliacaoService,
                    EventManager
                ]
            }).overrideComponent(AvaliacaoDetailComponent, {
                set: {
                    template: ''
                }
            }).compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(AvaliacaoDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AvaliacaoService);
        });


        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new Avaliacao(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.avaliacao).toEqual(jasmine.objectContaining({id:10}));
            });
        });
    });

});
