%% Benchmark#3.

clc; clear all; close all;
display(['Benchmark #3']);

%% Define search space.
X = 0:0.5:180;              % Theta (degree)
Y = 1:1:401   % Length (meter)
Z = [];                     % Directivity (dimensionless)

%% Compute directivity (NEC).
% (expected time: 441 seconds)
tic
for y = Y,
    display(['./CALLNEC.exe ' num2str(y)]);
    system(['./outputs/CALLNEC.exe ' num2str(y)]);
    z = dlmread('GAIN.OUT');
    system(['rm NEC.INP NEC.OUT GAIN.OUT']);
    Z = [Z 10.^(z(:,2)/10)];
end
Z = Z';
toc

%% Plot directivity without noise (NEC).
figure('Name', 'Benchmark#3 -  (NEC)');
Y=(Y-1)*0.01;
[X, Y] = meshgrid(X, Y);
surf(X, Y, Z, 'EdgeAlpha',0.3);
xlabel('Theta (degree)', 'FontSize', 20);
ylabel('Beta', 'FontSize', 20);
zlabel('Directivity (dimensionless)', 'FontSize', 20);

%% Save directivity (NEC).
dlmwrite('./outputs/directivity-b3.txt', Z);
save('./outputs/directivity-b3.m', 'X', 'Y', 'Z');

%% Plot fitted directivity (Cubic Interpolation).
% figure('Name', 'Benchmark#3 - without noise (NEC + Cubic Interpolation)');
% 
% step = 0.001;
% XX = 0:step:180;
% YY = 0:step:4;
% 
% [XX, YY] = meshgrid(XX, YY);
% ZZ = fitresult(XX(:), YY(:));
% ZZ = reshape(ZZ, size(XX));
% 
% surf(XX, YY, ZZ, 'EdgeAlpha', 0.3);
% xlabel('Theta (degree)', 'FontSize', 20);
% ylabel('Distance (meter)', 'FontSize', 20);
% zlabel('Directivity (dimensionless)', 'FontSize', 20);
% 
% %% Save directivity (Cubic Interpolation).
% ZZ_noise = ZZ + normrnd(0,sqrt(0.2), size(ZZ,1), size(ZZ,2));
% dlmwrite('./outputs/directivity-b3-ci.txt', ZZ_noise');
% save('./outputs/directivity-b3-ci.m', 'XX', 'YY', 'ZZ');


% %% Plot directivity with gaussian noise (mean=0,variance=2) (NEC).
% figure('Name', 'Benchmark#2 - with noise (NEC)');
% 
% surf(X, Y, Z + normrnd(0,sqrt(0.2), 201, 361), 'EdgeAlpha',0.3);
% xlabel('Theta (degree)', 'FontSize', 20);
% ylabel('Distance (meter)', 'FontSize', 20);
% zlabel('Directivity (dimensionless)', 'FontSize', 20);
% 
% %% Save directivity (NEC).
% dlmwrite('./outputs/directivity-b2.txt', Z);
% save('./outputs/directivity-b2.m', 'X', 'Y', 'Z');
% 
% %% Compute fitted directivity (cubic interpolation).
% opts = fitoptions('cubicinterp');
% opts.Weights = zeros(1,0);
% opts.Normalize = 'on';
% [fitresult, gof] = fit([X(:), Y(:)], Z(:), 'cubicinterp', opts);
% 
% %% Plot fitted directivity (Cubic Interpolation).
% figure('Name', 'Benchmark#2 - without noise (NEC + Cubic Interpolation)');
% 
% plot(fitresult, [X(:), Y(:)], Z(:));
% xlabel('Theta (degree)', 'FontSize', 20);
% ylabel('Distance (meter)', 'FontSize', 20);
% zlabel('Directivity (dimensionless)', 'FontSize', 20);
% 
% %% Plot fitted directivity (Cubic Interpolation).
% figure('Name', 'Benchmark#3 - without noise (NEC + Cubic Interpolation)');
% 
% step = 0.01;
% XX = 0:step:180;
% YY = 5:step:15;
% 
% [XX, YY] = meshgrid(XX, YY);
% ZZ = fitresult(XX(:), YY(:));
% ZZ = reshape(ZZ, size(XX));
% 
% surf(XX, YY, ZZ, 'EdgeAlpha', 0.3);
% xlabel('Theta (degree)', 'FontSize', 20);
% ylabel('Distance (meter)', 'FontSize', 20);
% zlabel('Directivity (dimensionless)', 'FontSize', 20);
% 
% %% Save directivity (Cubic Interpolation).
% ZZ_noise = ZZ + normrnd(0,sqrt(0.2), size(ZZ,1), size(ZZ,2));
% dlmwrite('./outputs/directivity-b2-ci.txt', ZZ_noise');
% save('./outputs/directivity-b2-ci.m', 'XX', 'YY', 'ZZ');
% 
